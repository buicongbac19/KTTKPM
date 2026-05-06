package com.ptit.restaurant_management_mono.service.strategy;

import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;

public interface PaymentStrategy {
    PaymentMethod supportedMethod();

    PaymentResult process(PaymentContext context);
}
