package com.ptit.restaurant_management_mono.exception;

/**
 * JSON lỗi tối giản (không dùng Map) — client chỉ cần trường message.
 */
public record LoiApi(String message) {
}
