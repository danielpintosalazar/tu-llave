package com.tullave.recharge.dto;

import com.tullave.recharge.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record RechargeRequest(

    @Schema(
        description = "16-digit TuLlave card number",
        example = "1234567890123456"
    )
    @NotBlank(message = "Card number is required")
    @Pattern(
        regexp = "\\d{16}",
        message = "Card number must contain exactly 16 digits"
    )
    String cardNumber,

    @Schema(
        description = "Recharge amount in COP",
        example = "50000",
        minimum = "2000",
        maximum = "200000"
    )
    @NotNull(message = "Amount is required")
    @DecimalMin(
        value = "2000.00",
        message = "Amount must be at least 2000"
    )
    @DecimalMax(
        value = "200000.00",
        message = "Amount must not exceed 200000"
    )
    BigDecimal amount,

    @Schema(
        description = "Payment method used for the recharge",
        example = "NEQUI"
    )
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod
) {}
