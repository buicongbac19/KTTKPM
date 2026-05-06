package com.ptit.restaurant_management_mono.service.strategy;

import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public record PaymentContext(PaymentMethod paymentMethod, BigDecimal totalAmountDue, BigDecimal amountReceived) {
}
