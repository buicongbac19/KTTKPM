package com.ptit.restaurant_management_mono.service.strategy;

public record PaymentResult(boolean success, String message, String transactionReference) {
}
