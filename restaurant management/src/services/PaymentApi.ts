import { apiRequest } from "@/services/http";
import type { BanDat, HoaDon, KhachHang } from "@/types/entities";

/** Chỉ cần hai trường định danh khách — khớp body {@link KhachHang} phía server. */
export function getOrCreateCustomer(
  khachHang: Pick<KhachHang, "hoVaTen" | "soDienThoai">,
): Promise<KhachHang> {
  return apiRequest<KhachHang>("/api/payments/customers/get-or-create", {
    method: "POST",
    body: khachHang,
  });
}

export function previewPayment(tableNumbers: number[]): Promise<BanDat[]> {
  return apiRequest<BanDat[]>("/api/payments/preview", {
    query: {
      soBanThanhToan: tableNumbers,
    },
  });
}

/** Phiếu xác nhận thanh toán — đối tượng {@link HoaDon} (các trường tối thiểu do client gửi). */
export function confirmPayment(phieuThanhToan: Partial<HoaDon>): Promise<HoaDon> {
  return apiRequest<HoaDon>("/api/payments/confirm", {
    method: "POST",
    body: phieuThanhToan,
  });
}

export function getInvoice(invoiceId: number): Promise<HoaDon> {
  return apiRequest<HoaDon>(`/api/payments/invoices/${invoiceId}`);
}
