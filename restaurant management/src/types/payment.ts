import type { BanDat, HoaDon, KhachHang, PaymentMethod } from "@/types/entities";

export type { BanDat, HoaDon, KhachHang, PaymentMethod };

export interface PaymentLineItemView {
  soBan: number;
  monAnId: number;
  tenMon: string;
  soLuong: number;
  donGia: number;
  thanhTien: number;
}

export interface PaymentPreviewView {
  soBanThanhToan: number[];
  items: PaymentLineItemView[];
  tienMon: number;
  tongTien: number;
}

export interface PaymentConfirmedView {
  hoaDonId: number;
  trangThai: string;
  phuongThucThanhToan: string;
  transactionReference: string;
  soBanDaThanhToan: number[];
  tongTien: number;
}

export interface InvoiceView {
  hoaDonId: number;
  tenKhachHang: string | null;
  soDienThoaiKhachHang: string | null;
  tenThuNgan: string | null;
  phuongThucThanhToan: string | null;
  /** Thời điểm sớm nhất trong các phiên bàn (thoiGianVao) — hoá đơn không có cột ngày theo thiết kế 5.2. */
  ngayHienThi: string | null;
  items: PaymentLineItemView[];
  tienMon: number;
  tongTien: number;
}
