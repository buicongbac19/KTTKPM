package com.ptit.restaurant_management_mono.service.strategy;

import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.THE;
    }

    @Override
    public PaymentResult process(PaymentContext context) {
        return new PaymentResult(true, "Thanh toán thẻ thành công.", "CARD-" + UUID.randomUUID());
    }
}
