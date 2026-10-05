package com.tullave.recharge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tullave.recharge.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;

public record RechargeResponse(

        @Schema(
            description = "Unique recharge identifier",
            example = "1"
        )
        Long id,

        @Schema(
            description = "16-digit TuLlave card number",
            example = "1234567890123456"
        )
        String cardNumber,

        @Schema(
            description = "Recharge amount in COP",
            example = "50000"
        )
        BigDecimal amount,

        @Schema(
            description = "Payment method used for the recharge",
            example = "NEQUI"
        )
        PaymentMethod paymentMethod,

        @Schema(
            description = "Date and time when the recharge was created",
            example = "2026-10-05T23:30:00"
        )
        LocalDateTime createdAt
) {
}
