package com.ptit.restaurant_management_mono.service.strategy;

import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.TIEN_MAT;
    }

    @Override
    public PaymentResult process(PaymentContext context) {
        BigDecimal amountReceived = context.amountReceived() == null ? BigDecimal.ZERO : context.amountReceived();
        if (amountReceived.compareTo(context.totalAmountDue()) < 0) {
            return new PaymentResult(false, "Số tiền khách trả không đủ.", null);
        }
        return new PaymentResult(true, "Thanh toán tiền mặt thành công.", "CASH-" + UUID.randomUUID());
    }
}
