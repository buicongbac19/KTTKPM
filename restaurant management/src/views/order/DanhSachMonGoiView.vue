<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import QuantityStepper from "@/components/QuantityStepper.vue";
import StatusBanner from "@/components/StatusBanner.vue";
import { getDishesByName } from "@/services/OrderApi";
import { useOrderStore } from "@/stores/orderStore";
import { formatCurrencyVnd } from "@/utils/format";
import type { MonAn } from "@/types/entities";

const route = useRoute();
const router = useRouter();
const orderStore = useOrderStore();

const keyword = ref("");
const searching = ref(false);
const resultItems = ref<MonAn[]>([]);
const rowInputs = reactive<Record<number, { quantity: number }>>({});

const tableLabel = computed(() => {
  const number = orderStore.session?.tableNumber;
  if (number == null) {
    return "--";
  }
  return String(number).padStart(2, "0");
});

function ensureRowInput(monAnId: number) {
  if (!rowInputs[monAnId]) {
    rowInputs[monAnId] = {
      quantity: 1,
    };
  }
  return rowInputs[monAnId];
}

onMounted(async () => {
  const sessionIdFromQuery = Number(route.query.sessionId);
  if (Number.isInteger(sessionIdFromQuery) && sessionIdFromQuery > 0) {
    try {
      await orderStore.loadReservationTable(sessionIdFromQuery);
    } catch {
      // Error state is already reflected in store.
    }
  }

  if (!orderStore.session) {
    await router.replace("/goi-mon/chon-ban");
    return;
  }

  await onSearch();
});

async function onSearch() {
  orderStore.clearMessages();
  searching.value = true;

  try {
    resultItems.value = await getDishesByName(keyword.value);
    resultItems.value.forEach((item) => {
      ensureRowInput(item.id);
    });
  } catch (error) {
    orderStore.errorMessage = error instanceof Error ? error.message : "Không thể tìm kiếm món ăn.";
  } finally {
    searching.value = false;
  }
}

async function onAddItem(menuItem: MonAn) {
  try {
    const input = ensureRowInput(menuItem.id);
    await orderStore.addItemToCurrentSession(menuItem.id, input.quantity);
  } catch (error) {
    orderStore.errorMessage =
      error instanceof Error ? error.message : "Không thể thêm món vào danh sách gọi món.";
  }
}

async function onConfirmOrder() {
  try {
    await orderStore.confirmCurrentOrder();
    window.alert("Đặt món thành công.");
    orderStore.resetOrderState();
    await router.push("/");
  } catch {
    // Error state is already reflected in store.
  }
}
</script>

<template>
  <main class="page menu-order-page">
    <section class="menu-order-panel">
      <header class="menu-order-header">
        <button
          class="menu-order-back"
          type="button"
          aria-label="Quay lại"
          @click="router.push('/goi-mon/chon-ban')"
        >
          ←
        </button>
        <h1 class="menu-order-title">Danh sách món gọi</h1>
      </header>
      <hr class="menu-order-divider" />

      <div class="menu-order-table-line">
        <span class="menu-order-table-label">Bàn số:</span>
        <span class="menu-order-table-num">{{ tableLabel }}</span>
      </div>

      <div class="menu-order-search">
        <input
          v-model="keyword"
          type="text"
          class="menu-order-search-input"
          placeholder="Nhập tên món ăn hoặc mã món"
          @keydown.enter.prevent="onSearch"
        />
        <button type="button" class="menu-order-btn-search" :disabled="searching" @click="onSearch">
          {{ searching ? "Đang tìm..." : "Tìm" }}
        </button>
      </div>

      <StatusBanner
        v-if="orderStore.errorMessage"
        variant="error"
        :message="orderStore.errorMessage"
      />
      <StatusBanner
        v-if="orderStore.successMessage"
        variant="success"
        :message="orderStore.successMessage"
      />

      <h2 class="menu-order-section-title">Kết quả tìm kiếm:</h2>
      <div class="table-shell menu-order-table-shell">
        <table class="data-table menu-table menu-order-data-table menu-order-results-table">
          <thead>
            <tr>
              <th class="col-dish-name">Tên món ăn</th>
              <th class="col-price">Giá</th>
              <th class="col-quantity">Số lượng</th>
              <th class="col-action">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="resultItems.length === 0">
              <td colspan="4" class="empty-row">Không tìm thấy món phù hợp.</td>
            </tr>
            <tr v-for="menuItem in resultItems" :key="menuItem.id">
              <td class="dish-name">{{ menuItem.ten }}</td>
              <td class="price-text">{{ formatCurrencyVnd(menuItem.gia) }}</td>
              <td class="menu-order-qty-cell">
                <QuantityStepper
                  :model-value="ensureRowInput(menuItem.id).quantity"
                  :min="1"
                  @update:model-value="ensureRowInput(menuItem.id).quantity = $event"
                />
              </td>
              <td class="menu-order-action-cell">
                <button type="button" class="menu-order-btn-add" @click="onAddItem(menuItem)">
                  Thêm
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <h2 class="menu-order-list-heading">DANH SÁCH MÓN ĂN ĐẶT</h2>
      <div class="table-shell menu-order-table-shell">
        <table class="data-table menu-order-data-table menu-order-order-table">
          <thead>
            <tr>
              <th class="col-stt">STT</th>
              <th>Tên món ăn</th>
              <th class="col-quantity">Số lượng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="orderStore.displayItems.length === 0">
              <td colspan="3" class="empty-row">Chưa có món nào trong danh sách gọi món.</td>
            </tr>
            <tr
              v-for="(item, index) in orderStore.displayItems"
              :key="`${item.itemId}-${item.monAnId}`"
            >
              <td>{{ Number(index) + 1 }}</td>
              <td>{{ item.tenMon }}</td>
              <td>{{ item.soLuong }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="menu-order-footer">
        <button
          type="button"
          class="menu-order-btn-confirm"
          :disabled="orderStore.submitting"
          @click="onConfirmOrder"
        >
          {{ orderStore.submitting ? "Đang xác nhận..." : "Xác nhận đặt món" }}
        </button>
      </div>
    </section>
  </main>
</template>
