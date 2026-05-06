<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { usePaymentStore } from "@/stores/paymentStore";
import { formatCurrencyVnd } from "@/utils/format";

const paymentStore = usePaymentStore();
const route = useRoute();
const router = useRouter();

const preview = computed(() => paymentStore.preview);

const tableNumbersLabel = computed(() => {
  if (!preview.value) {
    return "--";
  }
  return preview.value.soBanThanhToan
    .map((tableNumber: number) => String(tableNumber).padStart(2, "0"))
    .join(", ");
});

onMounted(async () => {
  const rawTables = route.query.tables;
  if (typeof rawTables === "string" && rawTables.trim().length > 0) {
    paymentStore.setTablesInput(rawTables);
  }

  if (!paymentStore.customerId) {
    await router.replace("/thanh-toan/nhap-thong-tin-khach-hang");
    return;
  }

  try {
    await paymentStore.loadPreviewForCurrentTables();
  } catch {
    // Error state is already reflected in store.
  }
});

async function onContinue() {
  if (!preview.value) {
    paymentStore.errorMessage = "Không có dữ liệu thanh toán để tiếp tục.";
    return;
  }

  await router.push("/thanh-toan/cho-thanh-toan");
}
</script>

<template>
  <main class="page payment-detail-page">
    <section v-if="preview" class="payment-detail-panel">
      <header class="payment-detail-header">
        <button
          class="payment-detail-back"
          type="button"
          aria-label="Quay lại"
          @click="router.push('/thanh-toan/nhap-thong-tin-khach-hang')"
        >
          ←
        </button>
        <h1 class="payment-detail-title">Chi tiết thanh toán</h1>
      </header>
      <hr class="payment-detail-divider" />

      <p class="payment-detail-banan-line">
        <span class="payment-detail-banan-text">
          Bàn thanh toán: <strong>{{ tableNumbersLabel }}</strong>
        </span>
      </p>

      <StatusBanner
        v-if="paymentStore.errorMessage"
        variant="error"
        :message="paymentStore.errorMessage"
      />

      <div class="table-shell payment-detail-table-shell">
        <table class="data-table payment-detail-data-table">
          <thead>
            <tr>
              <th class="col-stt">STT</th>
              <th>Tên món ăn</th>
              <th class="col-sl">SL</th>
              <th class="col-price">Đơn giá</th>
              <th class="col-price">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(item, index) in preview.items"
              :key="`${item.soBan}-${item.monAnId}-${index}`"
            >
              <td>{{ Number(index) + 1 }}</td>
              <td class="payment-detail-dish">{{ item.tenMon }}</td>
              <td>{{ item.soLuong }}</td>
              <td>{{ formatCurrencyVnd(item.donGia) }}</td>
              <td>{{ formatCurrencyVnd(item.thanhTien) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="payment-detail-summary-wrap">
        <div class="payment-detail-summary-box">
          <div class="payment-detail-summary-label">TỔNG CỘNG:</div>
          <div class="payment-detail-summary-value">{{ formatCurrencyVnd(preview.tongTien) }}</div>
        </div>
      </div>

      <section class="payment-detail-method-box">
        <p class="payment-detail-method-title">Hình thức thanh toán:</p>
        <div class="payment-detail-method-options">
          <label class="payment-detail-radio">
            <input v-model="paymentStore.paymentMethod" type="radio" value="TIEN_MAT" />
            Tiền mặt
          </label>
          <label class="payment-detail-radio">
            <input v-model="paymentStore.paymentMethod" type="radio" value="CHUYEN_KHOAN_QR" />
            Chuyển khoản QR
          </label>
          <label class="payment-detail-radio">
            <input v-model="paymentStore.paymentMethod" type="radio" value="THE" />
            Quẹt thẻ
          </label>
        </div>
      </section>

      <div class="payment-detail-footer">
        <button
          type="button"
          class="payment-detail-continue"
          :disabled="paymentStore.loadingPreview"
          @click="onContinue"
        >
          {{ paymentStore.loadingPreview ? "Đang tải..." : "TIẾP TỤC" }}
        </button>
      </div>
    </section>
  </main>
</template>
