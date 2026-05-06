/**
 * Kiểu JSON khớp các entity Java trả về từ API (Jackson camelCase).
 * Không dùng DTO riêng — map 1-1 với model phía server.
 */

export type BanDatTrangThai = "CHO_GOI_MON" | "DA_HOAN_THANH";

export type HoaDonTrangThai = "CHUA_THANH_TOAN" | "DA_THANH_TOAN";

export type PaymentMethod = "TIEN_MAT" | "CHUYEN_KHOAN_QR" | "THE";

export interface Ban {
  id: number;
  soNguoiToiDa: number;
  soThuTu: number;
}

export interface MonAn {
  id: number;
  ten: string;
  gia: number;
  moTa?: string | null;
  hinhAnh?: string | null;
}

/**
 * Món ăn đặt. Model có quan hệ tới BanDat; trong JSON lồng trong phiên bàn, `banDat` không được trả về (@JsonBackReference).
 */
export interface MonAnDat {
  id: number;
  soLuong: number;
  donGia: number;
  monAn: MonAn;
}

/**
 * Phiên bàn đặt. `hoaDon` dùng @JsonBackReference: khi load từ preview không lặp lên hoá đơn cha.
 */
export interface BanDat {
  id: number;
  ban: Ban;
  thoiGianVao: string;
  trangThai: BanDatTrangThai;
  monAnDats: MonAnDat[];
  hoaDon?: HoaDon | null;
}

export interface KhachHang {
  id: number;
  hoVaTen: string;
  soDienThoai: string;
}

export interface NguoiDung {
  id: number;
  username?: string | null;
  password?: string | null;
  hoVaTen: string;
  ngaySinh?: string | null;
  soDienThoai: string;
  email?: string | null;
}

export interface NhanVien extends NguoiDung {
  viTri?: string | null;
}

export interface HoaDon {
  id: number;
  tongTien: number;
  trangThai: HoaDonTrangThai;
  phuongThucThanhToan?: PaymentMethod | null;
  khachHang?: KhachHang | null;
  nhanVienThuNgan?: NhanVien | null;
  dsBanDat?: BanDat[] | null;
}
