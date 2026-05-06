<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { usePaymentStore } from "@/stores/paymentStore";

const paymentStore = usePaymentStore();
const router = useRouter();

const tablesInput = computed({
  get: () => paymentStore.tablesInput,
  set: (value: string) => {
    paymentStore.setTablesInput(value);
  },
});

async function onContinue() {
  try {
    const tables = paymentStore.ensureValidTablesInput(tablesInput.value);
    await router.push({
      path: "/thanh-toan/nhap-thong-tin-khach-hang",
      query: {
        tables: tables.join(","),
      },
    });
  } catch (error) {
    paymentStore.errorMessage = error instanceof Error ? error.message : "Không thể tiếp tục.";
  }
}
</script>

<template>
  <main class="page payment-tables-page">
    <section class="payment-tables-panel">
      <header class="payment-tables-header">
        <button
          class="payment-tables-back"
          type="button"
          aria-label="Quay lại"
          @click="router.push('/')"
        >
          ←
        </button>
        <h1 class="payment-tables-title">Nhập Các Bàn Thanh Toán</h1>
      </header>
      <hr class="payment-tables-divider" />

      <label class="payment-tables-label" for="tables-input">
        Nhập số bàn (cách nhau bởi dấu phẩy):
      </label>
      <input
        id="tables-input"
        v-model="tablesInput"
        class="payment-tables-input"
        type="text"
        placeholder="Ví dụ: 1, 5, 12"
        autocomplete="off"
        @keydown.enter.prevent="onContinue"
      />

      <StatusBanner
        v-if="paymentStore.errorMessage"
        variant="error"
        :message="paymentStore.errorMessage"
      />

      <div class="payment-tables-footer">
        <button
          type="button"
          class="payment-tables-continue"
          :disabled="paymentStore.loadingPreview"
          @click="onContinue"
        >
          {{ paymentStore.loadingPreview ? "Đang tải..." : "Tiếp tục" }}
        </button>
      </div>
    </section>
  </main>
</template>
