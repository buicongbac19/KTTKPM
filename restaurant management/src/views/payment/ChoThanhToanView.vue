<script setup lang="ts">
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { usePaymentStore } from "@/stores/paymentStore";

const paymentStore = usePaymentStore();
const router = useRouter();

onMounted(async () => {
  if (!paymentStore.preview || !paymentStore.customerId) {
    await router.replace("/thanh-toan/nhap-so-ban-thanh-toan");
  }
});

async function onConfirmPayment() {
  try {
    const confirmed = await paymentStore.confirmCurrentPayment();
    await router.push({
      path: "/thanh-toan/in-hoa-don",
      query: {
        invoiceId: confirmed.hoaDonId,
      },
    });
  } catch {
    // Error state is already reflected in store.
  }
}
</script>

<template>
  <main class="page payment-wait-page">
    <section class="payment-wait-panel">
      <header class="payment-wait-header">
        <button
          class="payment-wait-back"
          type="button"
          aria-label="Quay lại"
          @click="router.push('/thanh-toan/chi-tiet-thanh-toan')"
        >
          ←
        </button>
        <h1 class="payment-wait-title">Chờ thanh toán</h1>
      </header>
      <hr class="payment-wait-divider" />

      <div class="payment-wait-main">
        <p class="payment-wait-status">Đang chờ khách hàng thanh toán...</p>

        <StatusBanner
          v-if="paymentStore.errorMessage"
          variant="error"
          :message="paymentStore.errorMessage"
        />
      </div>

      <div class="payment-wait-footer">
        <button
          type="button"
          class="payment-wait-confirm"
          :disabled="paymentStore.submitting"
          @click="onConfirmPayment"
        >
          {{ paymentStore.submitting ? "Đang xử lý..." : "XÁC NHẬN THANH TOÁN" }}
        </button>
      </div>
    </section>
  </main>
</template>
