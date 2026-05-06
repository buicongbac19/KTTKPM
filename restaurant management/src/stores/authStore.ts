import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { login as loginApi } from "@/services/AuthApi";
import type { NguoiDungDangNhap } from "@/services/AuthApi";
import type { NhanVien } from "@/types/entities";

const STORAGE_KEY = "restaurant_auth_user";

export const useAuthStore = defineStore("auth", () => {
  const user = ref<NhanVien | null>(null);
  const loading = ref(false);

  const isAuthenticated = computed(() => user.value != null);

  function restoreSession() {
    if (typeof sessionStorage === "undefined") {
      return;
    }
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) {
      user.value = null;
      return;
    }
    try {
      user.value = JSON.parse(raw) as NhanVien;
    } catch {
      user.value = null;
      sessionStorage.removeItem(STORAGE_KEY);
    }
  }

  function persistUser(nhanVien: NhanVien) {
    user.value = nhanVien;
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(nhanVien));
  }

  function logout() {
    user.value = null;
    sessionStorage.removeItem(STORAGE_KEY);
  }

  async function login(nguoiDung: NguoiDungDangNhap): Promise<NhanVien> {
    loading.value = true;
    try {
      const result = await loginApi(nguoiDung);
      persistUser(result);
      return result;
    } finally {
      loading.value = false;
    }
  }

  return {
    user,
    loading,
    isAuthenticated,
    restoreSession,
    login,
    logout,
  };
});
