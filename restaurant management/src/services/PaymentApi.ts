import { apiRequest } from "@/services/http";
import type { HoaDon, KhachHang, MonAnDat, PaymentMethod } from "@/types/entities";

/** Khớp chữ ký thiết kế: {@code getOrCreateCustomer(soDienThoai, hoVaTen)} — tham số query. */
export function getOrCreateCustomer(soDienThoai: string, hoVaTen: string): Promise<KhachHang> {
  return apiRequest<KhachHang>("/api/payments/customers/get-or-create", {
    method: "POST",
    query: { soDienThoai, hoVaTen },
  });
}

/** {@code previewPayment(soBanThanhToan)} → {@link MonAnDat}[]. */
export function previewPayment(tableNumbers: number[]): Promise<MonAnDat[]> {
  return apiRequest<MonAnDat[]>("/api/payments/preview", {
    query: {
      soBanThanhToan: tableNumbers,
    },
  });
}

export interface ConfirmPaymentParams {
  soBanThanhToan: number[];
  khachHangId: number;
  thuNganId?: number;
  paymentMethod: PaymentMethod;
  /** Tiền khách đưa (tiền mặt), khớp {@code soTienKhachTra} — có thể bỏ qua. */
  soTienKhachTra?: number;
}

/** Khớp thiết kế: tham số query trên POST, không body {@link HoaDon}. */
export function confirmPayment(params: ConfirmPaymentParams): Promise<HoaDon> {
  const query: Record<string, string | number | number[] | undefined> = {
    soBanThanhToan: params.soBanThanhToan,
    khachHangId: params.khachHangId,
    paymentMethod: params.paymentMethod,
  };
  if (params.thuNganId != null) {
    query.thuNganId = params.thuNganId;
  }
  if (params.soTienKhachTra != null) {
    query.soTienKhachTra = params.soTienKhachTra;
  }
  return apiRequest<HoaDon>("/api/payments/confirm", {
    method: "POST",
    query,
  });
}

export function getInvoice(invoiceId: number): Promise<HoaDon> {
  return apiRequest<HoaDon>(`/api/payments/invoices/${invoiceId}`);
}
