import { apiRequest } from "@/services/http";
import type { NhanVien, NguoiDung } from "@/types/entities";

/**
 * Body đăng nhập: một đối tượng {@link NguoiDung} với đủ {@code username} và {@code password}
 * (server nhận kiểu cụ thể {@link NhanVien}).
 */
export type NguoiDungDangNhap = Required<Pick<NguoiDung, "username" | "password">>;

export function login(nguoiDung: NguoiDungDangNhap): Promise<NhanVien> {
  return apiRequest<NhanVien>("/api/auth/login", {
    method: "POST",
    body: nguoiDung,
  });
}
