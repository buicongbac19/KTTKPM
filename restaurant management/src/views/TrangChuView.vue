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
    </section>
  </main>
</template>
