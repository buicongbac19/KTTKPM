import { createRouter, createWebHistory } from "vue-router";
import TrangChuView from "@/views/TrangChuView.vue";
import ChonBanView from "@/views/order/ChonBanView.vue";
import DanhSachMonGoiView from "@/views/order/DanhSachMonGoiView.vue";
import NhapSoBanThanhToanView from "@/views/payment/NhapSoBanThanhToanView.vue";
import NhapThongTinKhachHangView from "@/views/payment/NhapThongTinKhachHangView.vue";
import ChiTietThanhToanView from "@/views/payment/ChiTietThanhToanView.vue";
import ChoThanhToanView from "@/views/payment/ChoThanhToanView.vue";
import InHoaDonView from "@/views/payment/InHoaDonView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "trang-chu",
      component: TrangChuView,
    },
    {
      path: "/goi-mon/chon-ban",
      name: "chon-ban",
      component: ChonBanView,
    },
    {
      path: "/goi-mon/danh-sach-mon-goi",
      name: "danh-sach-mon-goi",
      component: DanhSachMonGoiView,
    },
    {
      path: "/thanh-toan/nhap-so-ban-thanh-toan",
      name: "nhap-so-ban-thanh-toan",
      component: NhapSoBanThanhToanView,
    },
    {
      path: "/thanh-toan/nhap-thong-tin-khach-hang",
      name: "nhap-thong-tin-khach-hang",
      component: NhapThongTinKhachHangView,
    },
    {
      path: "/thanh-toan/chi-tiet-thanh-toan",
      name: "chi-tiet-thanh-toan",
      component: ChiTietThanhToanView,
    },
    {
      path: "/thanh-toan/cho-thanh-toan",
      name: "cho-thanh-toan",
      component: ChoThanhToanView,
    },
    {
      path: "/thanh-toan/in-hoa-don",
      name: "in-hoa-don",
      component: InHoaDonView,
    },
    {
      path: "/goi-mon",
      redirect: "/goi-mon/chon-ban",
    },
    {
      path: "/thanh-toan",
      redirect: "/thanh-toan/nhap-so-ban-thanh-toan",
    },
  ],
});

export default router;
