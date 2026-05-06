<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const authStore = useAuthStore();
const router = useRouter();

function onLogout() {
  authStore.logout();
  void router.replace("/dang-nhap");
}
</script>

<template>
  <main class="page">
    <section class="panel home-panel">
      <header class="home-header">
        <h1 class="home-title">Trang chủ</h1>
        <hr class="home-divider" />
      </header>

      <div class="home-actions">
        <RouterLink class="home-action home-action--order" to="/goi-mon/chon-ban">
          Cho khách gọi món
        </RouterLink>

        <RouterLink class="home-action home-action--pay" to="/thanh-toan/nhap-so-ban-thanh-toan">
          Thanh toán
        </RouterLink>
      </div>

      <p class="home-logout-wrap">
        <button type="button" class="home-logout" @click="onLogout">Đăng xuất</button>
      </p>
    </section>
  </main>
</template>
