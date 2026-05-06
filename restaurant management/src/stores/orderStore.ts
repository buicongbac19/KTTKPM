import { computed, ref } from "vue";
import { defineStore } from "pinia";
import {
  addItem,
  confirmOrder,
  getListTable,
  getOrCreateReservationTable,
  getReservationTable,
} from "@/services/OrderApi";
import type { Ban, BanDat, MonAnDat } from "@/types/entities";
import type { OrderLineItemView, OrderSessionView } from "@/types/order";

export const useOrderStore = defineStore("order", () => {
  const tables = ref<Ban[]>([]);
  const selectedTableNumber = ref<number | null>(null);
  const session = ref<OrderSessionView | null>(null);
  const loadingTables = ref(false);
  const loading = ref(false);
  const submitting = ref(false);
  const errorMessage = ref("");
  const successMessage = ref("");

  const canEditSession = computed(() => session.value?.sessionStatus === "CHO_GOI_MON");

  const displayItems = computed<OrderLineItemView[]>(() => {
    return session.value?.items ?? [];
  });

  const subtotal = computed(() => session.value?.subtotal ?? 0);

  function clearMessages() {
    errorMessage.value = "";
    successMessage.value = "";
  }

  function normalizeReservationSession(rawSession: BanDat): OrderSessionView {
    const monAnDats: MonAnDat[] = Array.isArray(rawSession.monAnDats) ? rawSession.monAnDats : [];

    const items: OrderLineItemView[] = monAnDats.map((item) => {
      const donGia = Number(item.donGia ?? 0);
      const soLuong = Number(item.soLuong ?? 0);
      return {
        itemId: Number(item.id ?? 0),
        monAnId: Number(item.monAn?.id ?? 0),
        tenMon: String(item.monAn?.ten ?? ""),
        donGia,
        soLuong,
        thanhTien: donGia * soLuong,
      };
    });

    const subtotalValue = items.reduce((sum, item) => sum + item.thanhTien, 0);

    return {
      sessionId: Number(rawSession.id ?? 0),
      tableNumber: Number(rawSession.ban?.soThuTu ?? 0),
      sessionStatus: String(rawSession.trangThai ?? "CHO_GOI_MON"),
      openedAt: String(rawSession.thoiGianVao ?? ""),
      items,
      subtotal: subtotalValue,
    };
  }

  async function loadTables(): Promise<Ban[]> {
    clearMessages();
    loadingTables.value = true;

    try {
      const result = await getListTable();
      tables.value = result;
      return result;
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : "Không thể tải danh sách bàn.";
      throw error;
    } finally {
      loadingTables.value = false;
    }
  }

  async function getOrCreateReservationBySelectedTable(): Promise<OrderSessionView> {
    clearMessages();
    loading.value = true;

    try {
      if (!Number.isInteger(selectedTableNumber.value) || (selectedTableNumber.value ?? 0) < 1) {
        throw new Error("Vui lòng chọn bàn hợp lệ trước khi tiếp tục.");
      }

      const rawSession = await getOrCreateReservationTable(selectedTableNumber.value as number);
      const normalizedSession = normalizeReservationSession(rawSession);
      session.value = normalizedSession;
      selectedTableNumber.value = normalizedSession.tableNumber;
      return normalizedSession;
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : "Không thể mở phiên gọi món.";
      throw error;
    } finally {
      loading.value = false;
    }
  }

  async function loadReservationTable(reservationTableId: number): Promise<OrderSessionView> {
    clearMessages();
    loading.value = true;

    try {
      const rawSession = await getReservationTable(reservationTableId);
      const normalizedSession = normalizeReservationSession(rawSession);
      session.value = normalizedSession;
      selectedTableNumber.value = normalizedSession.tableNumber;
      return normalizedSession;
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : "Không thể tải phiên gọi món.";
      throw error;
    } finally {
      loading.value = false;
    }
  }

  async function addItemToCurrentSession(monAnId: number, soLuong: number): Promise<OrderSessionView> {
    clearMessages();

    if (!session.value) {
      throw new Error("Vui lòng chọn bàn và mở phiên gọi món trước khi thêm món.");
    }

    if (!canEditSession.value) {
      throw new Error("Phiên này đã hoàn thành thanh toán, không thể thêm món mới.");
    }

    const normalizedQuantity = Math.max(1, Math.trunc(soLuong));

    submitting.value = true;

    try {
      const rawSession = await addItem(session.value.sessionId, monAnId, normalizedQuantity);
      const normalizedSession = normalizeReservationSession(rawSession);
      session.value = normalizedSession;
      successMessage.value = "Đã thêm món vào danh sách gọi món.";
      return normalizedSession;
    } catch (error) {
      errorMessage.value =
        error instanceof Error ? error.message : "Không thể thêm món vào danh sách gọi món.";
      throw error;
    } finally {
      submitting.value = false;
    }
  }

  async function confirmCurrentOrder(): Promise<OrderSessionView> {
    clearMessages();

    if (!session.value) {
      throw new Error("Chưa có phiên gọi món để xác nhận.");
    }

    if (!canEditSession.value) {
      throw new Error("Phiên gọi món đã hoàn thành thanh toán.");
    }

    if (session.value.items.length === 0) {
      throw new Error("Danh sách món đang trống. Vui lòng thêm món trước khi xác nhận.");
    }

    submitting.value = true;

    try {
      const rawSession = await confirmOrder(session.value.sessionId);
      const confirmedSession = normalizeReservationSession(rawSession);
      session.value = confirmedSession;
      successMessage.value = "Đặt món thành công. Đơn đã được gửi xuống bếp.";
      return confirmedSession;
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : "Không thể xác nhận đặt món.";
      throw error;
    } finally {
      submitting.value = false;
    }
  }

  function resetOrderState() {
    selectedTableNumber.value = null;
    session.value = null;
    clearMessages();
  }

  return {
    tables,
    selectedTableNumber,
    session,
    loadingTables,
    loading,
    submitting,
    errorMessage,
    successMessage,
    canEditSession,
    displayItems,
    subtotal,
    clearMessages,
    loadTables,
    getOrCreateReservationBySelectedTable,
    loadReservationTable,
    addItemToCurrentSession,
    confirmCurrentOrder,
    resetOrderState,
  };
});
