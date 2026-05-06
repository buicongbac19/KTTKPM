<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { usePaymentStore } from "@/stores/paymentStore";
import { formatCurrencyVnd, formatDateTime, paymentMethodLabel } from "@/utils/format";
import type { PaymentLineItemView } from "@/types/payment";

const paymentStore = usePaymentStore();
const route = useRoute();
const router = useRouter();

const invoice = computed(() => paymentStore.invoice);

const tableNumbers = computed(() => {
  if (!invoice.value) {
    return [];
  }

  const uniqueTableNumbers = new Set<number>();
  invoice.value.items.forEach((item: PaymentLineItemView) => {
    uniqueTableNumbers.add(item.soBan);
  });

  return Array.from(uniqueTableNumbers).sort((a, b) => a - b);
});

const tableNumberLabel = computed(() => {
  if (tableNumbers.value.length === 0) {
    return "--";
  }

  return tableNumbers.value.map((item) => String(item).padStart(2, "0")).join(", ");
});

onMounted(async () => {
  const invoiceIdFromQuery = Number(route.query.invoiceId);
  const invoiceId =
    Number.isInteger(invoiceIdFromQuery) && invoiceIdFromQuery > 0
      ? invoiceIdFromQuery
      : paymentStore.confirmed?.hoaDonId;

  if (!invoiceId) {
    await router.replace("/thanh-toan/nhap-so-ban-thanh-toan");
    return;
  }

  if (invoice.value?.hoaDonId === invoiceId) {
    return;
  }

  try {
    await paymentStore.loadInvoiceById(invoiceId);
  } catch {
    // Error state is already reflected in store.
  }
});

/** Xác nhận đã in (không mở hộp thoại in của trình duyệt). Hóa đơn đã được lưu khi thanh toán. */
function onInvoicePrinted() {
  window.alert("In hóa đơn thành công.");
  paymentStore.resetPaymentState();
  void router.push("/");
}
</script>

<template>
  <main class="page invoice-print-page">
    <section v-if="invoice" class="invoice-print-panel">
      <header class="invoice-print-header no-print">
        <button
          class="invoice-print-back"
          type="button"
          aria-label="Quay lại"
          @click="router.push('/')"
        >
          ←
        </button>
        <h1 class="invoice-print-title">In hóa đơn</h1>
      </header>
      <hr class="invoice-print-divider no-print" />

      <article class="invoice-paper">
        <h2 class="invoice-receipt-title">HÓA ĐƠN THANH TOÁN</h2>
        <p class="invoice-receipt-store">Nhà hàng Dê 35</p>
        <p class="invoice-receipt-store">Địa chỉ: Sơn Tây, Hà Nội</p>

        <hr class="invoice-dash" />

        <div class="invoice-line">
          <span>Ngày:</span><span>{{ formatDateTime(invoice.ngayHienThi) }}</span>
        </div>
        <div class="invoice-line">
          <span>Bàn số:</span><span>{{ tableNumberLabel }}</span>
        </div>
        <div class="invoice-line">
          <span>Thu ngân:</span><span>{{ invoice.tenThuNgan ?? "Nguyễn Văn A" }}</span>
        </div>
        <div class="invoice-line">
          <span>Khách hàng:</span><strong>{{ invoice.tenKhachHang ?? "--" }}</strong>
        </div>

        <hr class="invoice-dash" />

        <div
          v-for="(item, index) in invoice.items"
          :key="`${item.soBan}-${item.monAnId}-${index}`"
          class="invoice-line invoice-line-item"
        >
          <span>{{ item.tenMon }} x{{ item.soLuong }}</span>
          <span>{{ formatCurrencyVnd(item.thanhTien) }}</span>
        </div>

        <hr class="invoice-dash" />

        <div class="invoice-line invoice-line-total">
          <strong>TỔNG CỘNG:</strong>
          <strong>{{ formatCurrencyVnd(invoice.tongTien) }} VNĐ</strong>
        </div>

        <div class="invoice-line">
          <span>Hình thức:</span><span>{{ paymentMethodLabel(invoice.phuongThucThanhToan) }}</span>
        </div>

        <hr class="invoice-dash" />

        <p class="invoice-thanks">Cảm ơn Quý khách. Hẹn gặp lại!</p>
      </article>

      <StatusBanner
        v-if="paymentStore.errorMessage"
        variant="error"
        class="no-print"
        :message="paymentStore.errorMessage"
      />

      <button class="invoice-print-btn no-print" type="button" @click="onInvoicePrinted">
        In hoá đơn
      </button>
    </section>
  </main>
</template>
