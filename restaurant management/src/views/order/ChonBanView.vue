<script setup lang="ts">
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { useOrderStore } from "@/stores/orderStore";

const store = useOrderStore();
const router = useRouter();

onMounted(async () => {
  try {
    await store.loadTables();
  } catch {
    // Error state is already reflected in store.
  }
});

async function onContinue() {
  try {
    const session = await store.getOrCreateReservationBySelectedTable();
    await router.push({
      path: "/goi-mon/danh-sach-mon-goi",
      query: {
        sessionId: session.sessionId,
      },
    });
  } catch {
    // Error state is already reflected in store.
  }
}
</script>

<template>
  <main class="page choose-table-page">
    <section class="choose-table-panel">
      <header class="choose-table-header">
        <button class="choose-table-back" type="button" aria-label="Quay lại" @click="router.push('/')">
          ←
        </button>
        <h1 class="choose-table-title">Chọn bàn</h1>
      </header>
      <hr class="choose-table-divider" />

      <label class="choose-table-field-label" for="table-select">Chọn bàn từ danh sách:</label>
      <div class="choose-table-select-wrap">
        <select
          id="table-select"
          v-model.number="store.selectedTableNumber"
          class="choose-table-select"
          :disabled="store.loadingTables || store.loading"
        >
          <option :value="null">-- Chọn số bàn --</option>
          <option v-for="table in store.tables" :key="table.id" :value="table.soThuTu">
            Bàn {{ table.soThuTu }} ({{ table.soNguoiToiDa }} chỗ)
          </option>
        </select>
      </div>

      <StatusBanner v-if="store.errorMessage" variant="error" :message="store.errorMessage" />

      <div class="choose-table-footer">
        <button
          class="choose-table-continue"
          type="button"
          :disabled="store.loading || store.loadingTables"
          @click="onContinue"
        >
          {{ store.loading ? "Đang xử lý..." : "Tiếp tục" }}
        </button>
      </div>
    </section>
  </main>
</template>
