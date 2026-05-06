<script setup lang="ts">
const props = defineProps<{
  message: string
  variant: 'error' | 'success'
}>()
</script>

<template>
  <p class="status-banner" :class="variant">
    {{ props.message }}
  </p>
</template>

<style scoped>
.status-banner {
  margin: 0;
  border: 1px solid;
  padding: 10px 12px;
  font-size: 14px;
  font-weight: 600;
}

.status-banner.error {
  color: #7b0d0d;
  border-color: #c77878;
  background: #f8e5e5;
}

.status-banner.success {
  color: #154f1f;
  border-color: #89b18f;
  background: #e6f1e7;
}
</style>
