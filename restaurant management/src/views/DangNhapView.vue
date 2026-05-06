<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import StatusBanner from "@/components/StatusBanner.vue";
import { useAuthStore } from "@/stores/authStore";

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const username = ref("");
const password = ref("");
const touched = reactive({ username: false, password: false });
const submitAttempted = ref(false);
const apiError = ref("");

const usernameError = computed(() => {
  const value = username.value.trim();
  if (!submitAttempted.value && !touched.username) {
    return "";
  }
  if (value.length === 0) {
    return "Tên đăng nhập không được để trống.";
  }
  if (value.length > 100) {
    return "Tên đăng nhập không được vượt quá 100 ký tự.";
  }
  if (!/^[\w.@+-]+$/.test(value)) {
    return "Tên đăng nhập chỉ gồm chữ, số và . _ @ + -";
  }
  return "";
});

const passwordError = computed(() => {
  const value = password.value;
  if (!submitAttempted.value && !touched.password) {
    return "";
  }
  if (value.length === 0) {
    return "Mật khẩu không được để trống.";
  }
  if (value.length < 4) {
    return "Mật khẩu phải có ít nhất 4 ký tự.";
  }
  if (value.length > 200) {
    return "Mật khẩu không được vượt quá 200 ký tự.";
  }
  return "";
});

const formValid = computed(
  () =>
    usernameError.value === "" &&
    passwordError.value === "" &&
    username.value.trim().length > 0 &&
    password.value.length > 0,
);

async function onSubmit() {
  submitAttempted.value = true;
  touched.username = true;
  touched.password = true;
  apiError.value = "";

  if (!formValid.value) {
    return;
  }

  try {
    await authStore.login({
      username: username.value.trim(),
      password: password.value,
    });
    const rawRedirect = route.query.redirect;
    const redirect =
      typeof rawRedirect === "string"
        ? rawRedirect
        : Array.isArray(rawRedirect) && typeof rawRedirect[0] === "string"
          ? rawRedirect[0]
          : "/";
    const safePath = redirect.startsWith("/") && !redirect.startsWith("//") ? redirect : "/";
    await router.replace(safePath);
  } catch (error) {
    apiError.value = error instanceof Error ? error.message : "Đăng nhập thất bại.";
  }
}
</script>

<template>
  <main class="page login-page">
    <section class="login-panel">
      <header class="login-header">
        <h1 class="login-title">Đăng nhập</h1>
      </header>
      <hr class="login-divider" />

      <form class="login-form" novalidate @submit.prevent="onSubmit">
        <div class="login-field">
          <label class="login-label" for="login-username">Username</label>
          <input
            id="login-username"
            v-model="username"
            class="login-input"
            type="text"
            autocomplete="username"
            maxlength="100"
            @blur="touched.username = true"
          />
          <p v-if="usernameError" class="login-field-error">{{ usernameError }}</p>
        </div>

        <div class="login-field">
          <label class="login-label" for="login-password">Password</label>
          <input
            id="login-password"
            v-model="password"
            class="login-input"
            type="password"
            autocomplete="current-password"
            maxlength="200"
            @blur="touched.password = true"
          />
          <p v-if="passwordError" class="login-field-error">{{ passwordError }}</p>
        </div>

        <StatusBanner v-if="apiError" variant="error" :message="apiError" />

        <div class="login-footer">
          <button type="submit" class="login-submit" :disabled="authStore.loading">
            {{ authStore.loading ? "Đang đăng nhập..." : "Đăng nhập" }}
          </button>
        </div>
      </form>
    </section>
  </main>
</template>
