import type { Ban, BanDat, MonAn } from "@/types/entities";

export type { Ban, BanDat, MonAn };

/** Dòng hiển thị trong bảng món gọi (tổng hợp từ MonAnDat). */
export interface OrderLineItemView {
  itemId: number;
  monAnId: number;
  tenMon: string;
  donGia: number;
  soLuong: number;
  thanhTien: number;
}

/** Trạng thái phiên gọi món trong store (chuẩn hoá từ BanDat). */
export interface OrderSessionView {
  sessionId: number;
  tableNumber: number;
  sessionStatus: string;
  openedAt: string;
  items: OrderLineItemView[];
  subtotal: number;
}
