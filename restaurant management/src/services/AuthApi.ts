import { apiRequest } from "@/services/http";
import type { NguoiDung, NhanVien } from "@/types/entities";

export type NguoiDungDangNhap = Required<Pick<NguoiDung, "username" | "password">>;

export function login(nguoiDung: NguoiDungDangNhap): Promise<NhanVien> {
  return apiRequest<NhanVien>("/api/auth/login", {
    method: "POST",
    body: nguoiDung,
  });
}
