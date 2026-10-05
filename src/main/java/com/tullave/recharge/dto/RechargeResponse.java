package com.tullave.recharge.dto;

import com.tullave.recharge.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RechargeResponse(
        Long id,
        String cardNumber,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt
) {
}
