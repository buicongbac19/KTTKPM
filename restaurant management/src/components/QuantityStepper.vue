<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    modelValue: number
    min?: number
    disabled?: boolean
  }>(),
  {
    min: 1,
    disabled: false,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: number]
}>()

function decrease() {
  if (props.disabled) {
    return
  }
  const next = Math.max(props.min, props.modelValue - 1)
  emit('update:modelValue', next)
}

function increase() {
  if (props.disabled) {
    return
  }
  emit('update:modelValue', props.modelValue + 1)
}
</script>

<template>
  <div class="quantity-stepper">
    <button type="button" class="step-btn" :disabled="disabled" @click="decrease">-</button>
    <input
      class="quantity-input"
      type="number"
      :value="modelValue"
      :min="min"
      :disabled="disabled"
      @input="emit('update:modelValue', Math.max(min, Number(($event.target as HTMLInputElement).value) || min))"
    />
    <button type="button" class="step-btn" :disabled="disabled" @click="increase">+</button>
  </div>
</template>

<style scoped>
.quantity-stepper {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.step-btn {
  width: 28px;
  height: 28px;
  border: 1px solid var(--border-strong);
  background: var(--panel-bg);
  font-weight: 700;
  cursor: pointer;
}

.step-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.quantity-input {
  width: 40px;
  height: 28px;
  border: 1px solid var(--border-strong);
  text-align: center;
  font-weight: 700;
  background: var(--panel-bg);
}

.quantity-input::-webkit-outer-spin-button,
.quantity-input::-webkit-inner-spin-button {
  appearance: none;
  margin: 0;
}
</style>
