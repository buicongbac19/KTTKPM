<script setup lang="ts">
import { onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { usePaymentStore } from "@/stores/paymentStore";

const paymentStore = usePaymentStore();
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  const rawTables = route.query.tables;
  if (typeof rawTables === "string" && rawTables.trim().length > 0) {
    paymentStore.setTablesInput(rawTables);
  }

  if (paymentStore.parsedTableNumbers.length === 0) {
    await router.replace("/thanh-toan/nhap-so-ban-thanh-toan");
  }
});

async function onContinue() {
  try {
    await paymentStore.getOrCreateCurrentCustomer();
    await router.push({
      path: "/thanh-toan/chi-tiet-thanh-toan",
      query: {
        tables: paymentStore.parsedTableNumbers.join(","),
      },
    });
  } catch (error) {
    paymentStore.errorMessage =
      error instanceof Error ? error.message : "Không thể lưu khách hàng.";
  }
}
</script>

<template>
  <main class="page customer-info-page">
    <section class="customer-info-panel">
      <header class="customer-info-header">
        <button
          class="customer-info-back"
          type="button"
          aria-label="Quay lại"
          @click="router.push('/thanh-toan/nhap-so-ban-thanh-toan')"
        >
          ←
        </button>
        <h1 class="customer-info-title">Nhập thông tin khách hàng</h1>
      </header>
      <hr class="customer-info-divider" />

      <div class="customer-info-field">
        <label class="customer-info-label" for="customer-name">Tên khách hàng:</label>
        <input
          id="customer-name"
          v-model="paymentStore.customerName"
          class="customer-info-input"
          type="text"
          autocomplete="name"
          placeholder="Nhập họ và tên..."
        />
      </div>

      <div class="customer-info-field customer-info-field--gap">
        <label class="customer-info-label" for="customer-phone">Số điện thoại:</label>
        <input
          id="customer-phone"
          v-model="paymentStore.customerPhone"
          class="customer-info-input"
          type="tel"
          inputmode="tel"
          autocomplete="tel"
          placeholder="Nhập số điện thoại..."
        />
      </div>

      <StatusBanner
        v-if="paymentStore.errorMessage"
        variant="error"
        :message="paymentStore.errorMessage"
      />

      <div class="customer-info-footer">
        <button
          type="button"
          class="customer-info-continue"
          :disabled="paymentStore.loadingCustomer"
          @click="onContinue"
        >
          {{ paymentStore.loadingCustomer ? "Đang xử lý..." : "Tiếp tục" }}
        </button>
      </div>
    </section>
  </main>
</template>
