import { computed, ref } from "vue";
import { defineStore } from "pinia";
import {
  confirmPayment,
  getInvoice,
  getOrCreateCustomer,
  previewPayment,
} from "@/services/PaymentApi";
import type { BanDat, HoaDon, KhachHang, MonAnDat, PaymentMethod } from "@/types/entities";
import type {
  InvoiceView,
  PaymentConfirmedView,
  PaymentLineItemView,
  PaymentPreviewView,
} from "@/types/payment";

export const usePaymentStore = defineStore("payment", () => {
  const tablesInput = ref("");
  const preview = ref<PaymentPreviewView | null>(null);
  const invoice = ref<InvoiceView | null>(null);
  const confirmed = ref<PaymentConfirmedView | null>(null);

  const customerId = ref<number | null>(null);
  const customerName = ref("");
  const customerPhone = ref("");

  const paymentMethod = ref<PaymentMethod>("TIEN_MAT");
  const amountReceived = ref("");

  const loadingPreview = ref(false);
  const loadingCustomer = ref(false);
  const submitting = ref(false);
  const loadingInvoice = ref(false);
  const errorMessage = ref("");
  const successMessage = ref("");

  const parsedTableNumbers = computed(() => parseTableNumbers(tablesInput.value));

  function clearMessages() {
    errorMessage.value = "";
    successMessage.value = "";
  }

  function parseTableNumbers(raw: string): number[] {
    const values = raw
      .split(",")
      .map((token) => token.trim())
      .filter((token) => token.length > 0)
      .map((token) => Number.parseInt(token, 10))
      .filter((number) => Number.isInteger(number) && number > 0);

    return Array.from(new Set(values)).sort((a, b) => a - b);
  }

  function setTablesInput(raw: string) {
    tablesInput.value = raw;
  }

  function normalizePreview(rawSessions: BanDat[], tableNumbers: number[]): PaymentPreviewView {
    const sessions = rawSessions ?? [];

    const items: PaymentLineItemView[] = sessions.flatMap((session) => {
      const monAnDats: MonAnDat[] = Array.isArray(session.monAnDats) ? session.monAnDats : [];
      return monAnDats.map((item) => {
        const donGia = Number(item.donGia ?? 0);
        const soLuong = Number(item.soLuong ?? 0);
        return {
          soBan: Number(session.ban?.soThuTu ?? 0),
          monAnId: Number(item.monAn?.id ?? 0),
          tenMon: String(item.monAn?.ten ?? ""),
          soLuong,
          donGia,
          thanhTien: donGia * soLuong,
        };
      });
    });

    const tongTien = items.reduce((sum, item) => sum + item.thanhTien, 0);
    return {
      soBanThanhToan: tableNumbers,
      items,
      tienMon: tongTien,
      tongTien,
    };
  }

  function normalizeHoaDonResponse(rawHoaDon: HoaDon): PaymentConfirmedView {
    const dsBanDat = Array.isArray(rawHoaDon.dsBanDat) ? rawHoaDon.dsBanDat : [];

    const soBanDaThanhToan = dsBanDat
      .map((session) => Number(session.ban?.soThuTu ?? 0))
      .filter((n) => Number.isInteger(n) && n > 0);

    return {
      hoaDonId: Number(rawHoaDon.id ?? 0),
      trangThai: String(rawHoaDon.trangThai ?? ""),
      phuongThucThanhToan: String(rawHoaDon.phuongThucThanhToan ?? ""),
      transactionReference: `HD-${Number(rawHoaDon.id ?? 0)}`,
      soBanDaThanhToan: Array.from(new Set(soBanDaThanhToan)),
      tongTien: Number(rawHoaDon.tongTien ?? 0),
    };
  }

  function normalizeInvoice(rawHoaDon: HoaDon): InvoiceView {
    const dsBanDat = Array.isArray(rawHoaDon.dsBanDat) ? rawHoaDon.dsBanDat : [];

    const items: PaymentLineItemView[] = dsBanDat.flatMap((session) => {
      const monAnDats: MonAnDat[] = Array.isArray(session.monAnDats) ? session.monAnDats : [];
      return monAnDats.map((item) => {
        const donGia = Number(item.donGia ?? 0);
        const soLuong = Number(item.soLuong ?? 0);
        return {
          soBan: Number(session.ban?.soThuTu ?? 0),
          monAnId: Number(item.monAn?.id ?? 0),
          tenMon: String(item.monAn?.ten ?? ""),
          soLuong,
          donGia,
          thanhTien: donGia * soLuong,
        };
      });
    });

    const tienMon = items.reduce((sum, item) => sum + item.thanhTien, 0);
    const thoiGianVaoCacPhien = dsBanDat
      .map((s) => s.thoiGianVao)
      .filter((t): t is string => typeof t === "string" && t.length > 0);
    const ngayHienThi =
      thoiGianVaoCacPhien.length > 0
        ? thoiGianVaoCacPhien.sort()[0] ?? null
        : null;

    return {
      hoaDonId: Number(rawHoaDon.id ?? 0),
      tenKhachHang: rawHoaDon.khachHang?.hoVaTen ?? null,
      soDienThoaiKhachHang: rawHoaDon.khachHang?.soDienThoai ?? null,
      tenThuNgan: rawHoaDon.nhanVienThuNgan?.hoVaTen ?? null,
      phuongThucThanhToan: rawHoaDon.phuongThucThanhToan ?? null,
      ngayHienThi,
      items,
      tienMon,
      tongTien: Number(rawHoaDon.tongTien ?? 0),
    };
  }

  function ensureValidTablesInput(raw: string): number[] {
    const tableNumbers = parseTableNumbers(raw);
    if (tableNumbers.length === 0) {
      throw new Error("Vui lòng nhập tối thiểu một số bàn hợp lệ.");
    }

    tablesInput.value = tableNumbers.join(", ");
    return tableNumbers;
  }

  async function getOrCreateCurrentCustomer(): Promise<number> {
    clearMessages();

    const name = customerName.value.trim();
    const phone = customerPhone.value.trim();

    if (name.length === 0) {
      throw new Error("Vui lòng nhập tên khách hàng.");
    }

    if (phone.length === 0) {
      throw new Error("Vui lòng nhập số điện thoại khách hàng.");
    }

    loadingCustomer.value = true;

    try {
      const result = await getOrCreateCustomer({
        hoVaTen: name,
        soDienThoai: phone,
      });
      customerId.value = result.id;
      customerName.value = result.hoVaTen;
      customerPhone.value = result.soDienThoai;
      successMessage.value = "Đã lưu thông tin khách hàng.";
      return result.id;
    } catch (error) {
      errorMessage.value =
        error instanceof Error ? error.message : "Không thể lưu thông tin khách hàng.";
      throw error;
    } finally {
      loadingCustomer.value = false;
    }
  }

  async function loadPreviewForCurrentTables(): Promise<PaymentPreviewView> {
    clearMessages();

    const tableNumbers = parseTableNumbers(tablesInput.value);
    if (tableNumbers.length === 0) {
      throw new Error("Vui lòng nhập tối thiểu một số bàn hợp lệ.");
    }

    loadingPreview.value = true;

    try {
      const rawSessions = await previewPayment(tableNumbers);
      const result = normalizePreview(rawSessions, tableNumbers);
      preview.value = result;
      confirmed.value = null;
      invoice.value = null;
      if (amountReceived.value.trim().length === 0) {
        amountReceived.value = String(result.tongTien);
      }
      return result;
    } catch (error) {
      errorMessage.value =
        error instanceof Error ? error.message : "Không thể tải dữ liệu thanh toán.";
      throw error;
    } finally {
      loadingPreview.value = false;
    }
  }

  async function confirmCurrentPayment(): Promise<PaymentConfirmedView> {
    clearMessages();

    if (!preview.value) {
      throw new Error("Chưa có dữ liệu chi tiết thanh toán.");
    }

    if (!customerId.value) {
      throw new Error("Vui lòng nhập thông tin khách hàng trước khi xác nhận thanh toán.");
    }

    submitting.value = true;

    try {
      const phieuThanhToan: Partial<HoaDon> = {
        phuongThucThanhToan: paymentMethod.value,
        khachHang: customerId.value ? ({ id: customerId.value } as KhachHang) : undefined,
        dsBanDat: preview.value.soBanThanhToan.map((soThuTu) => ({
          ban: { soThuTu },
        })) as HoaDon["dsBanDat"],
      };

      if (paymentMethod.value === "TIEN_MAT") {
        const numericAmount =
          amountReceived.value.trim().length > 0
            ? Number.parseFloat(amountReceived.value.replace(",", "."))
            : preview.value.tongTien;
        const tien =
          Number.isFinite(numericAmount) ? numericAmount : preview.value.tongTien;
        phieuThanhToan.tongTien = tien;
      }

      const rawHoaDon = await confirmPayment(phieuThanhToan);
      const result = normalizeHoaDonResponse(rawHoaDon);
      confirmed.value = result;
      successMessage.value = "Xác nhận thanh toán thành công.";
      return result;
    } catch (error) {
      errorMessage.value =
        error instanceof Error ? error.message : "Không thể xác nhận thanh toán.";
      throw error;
    } finally {
      submitting.value = false;
    }
  }

  async function loadInvoiceById(invoiceId: number): Promise<InvoiceView> {
    clearMessages();
    loadingInvoice.value = true;

    try {
      const rawHoaDon = await getInvoice(invoiceId);
      const result = normalizeInvoice(rawHoaDon);
      invoice.value = result;
      return result;
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : "Không thể tải hóa đơn.";
      throw error;
    } finally {
      loadingInvoice.value = false;
    }
  }

  function resetPaymentState() {
    tablesInput.value = "";
    preview.value = null;
    invoice.value = null;
    confirmed.value = null;
    customerId.value = null;
    customerName.value = "";
    customerPhone.value = "";
    paymentMethod.value = "TIEN_MAT";
    amountReceived.value = "";
    clearMessages();
  }

  return {
    tablesInput,
    preview,
    invoice,
    confirmed,
    customerId,
    customerName,
    customerPhone,
    paymentMethod,
    amountReceived,
    loadingPreview,
    loadingCustomer,
    submitting,
    loadingInvoice,
    errorMessage,
    successMessage,
    parsedTableNumbers,
    clearMessages,
    setTablesInput,
    ensureValidTablesInput,
    getOrCreateCurrentCustomer,
    loadPreviewForCurrentTables,
    confirmCurrentPayment,
    loadInvoiceById,
    resetPaymentState,
  };
});
