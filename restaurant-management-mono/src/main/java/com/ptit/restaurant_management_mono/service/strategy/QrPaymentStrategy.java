package com.ptit.restaurant_management_mono.service.strategy;

import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class QrPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.CHUYEN_KHOAN_QR;
    }

    @Override
    public PaymentResult process(PaymentContext context) {
        return new PaymentResult(true, "Thanh toán QR thành công.", "QR-" + UUID.randomUUID());
    }
}
