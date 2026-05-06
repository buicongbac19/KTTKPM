<template>
  <header v-if="showTopNav" class="top-nav no-print">
    <RouterLink class="top-nav-link" to="/">Trang chủ</RouterLink>
  </header>

  <RouterView />
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const showTopNav = computed(() => route.name !== "trang-chu");
</script>
