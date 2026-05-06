<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{
  src: string | null
  alt: string
}>()

const failed = ref(false)

watch(
  () => props.src,
  () => {
    failed.value = false
  },
)

function onError() {
  failed.value = true
}

const imageSrc = computed(() => {
  if (!props.src || failed.value) {
    const initials = props.alt
      .split(' ')
      .filter((word) => word.length > 0)
      .slice(0, 2)
      .map((word) => word[0]?.toUpperCase() ?? '')
      .join('')

    const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='100' height='70'><rect width='100%' height='100%' fill='#e6e6e6'/><text x='50%' y='55%' dominant-baseline='middle' text-anchor='middle' fill='#555' font-family='Arial' font-size='20' font-weight='700'>${initials || 'MA'}</text></svg>`
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`
  }

  return props.src
})
</script>

<template>
  <img class="dish-image" :src="imageSrc" :alt="alt" @error="onError" />
</template>

<style scoped>
.dish-image {
  width: 88px;
  height: 60px;
  border: 1px solid var(--border-default);
  object-fit: cover;
  background: #efefef;
}
</style>
