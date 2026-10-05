package com.tullave.recharge.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.tullave.recharge.dto.RechargeResponse;
import com.tullave.recharge.enums.PaymentMethod;
import com.tullave.recharge.service.RechargeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import com.tullave.recharge.exception.RechargeNotFoundException;

import static org.mockito.Mockito.doThrow;

@WebMvcTest(RechargeController.class)
class RechargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RechargeService rechargeService;

    @Test
    void shouldCreateRecharge() throws Exception {
        RechargeResponse response = new RechargeResponse(
                1L,
                "1234567890123456",
                new BigDecimal("50000"),
                PaymentMethod.NEQUI,
                LocalDateTime.now());

        when(rechargeService.createRecharge(any()))
                .thenReturn(response);

        String requestBody = """
                {
                    "cardNumber": "1234567890123456",
                    "amount": 50000,
                    "paymentMethod": "NEQUI"
                }
                """;

        mockMvc.perform(post("/api/v1/recharges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.paymentMethod").value("NEQUI"));
    }

    @Test
    void shouldReturnBadRequestWhenCardNumberIsInvalid() throws Exception {
        String requestBody = """
                {
                    "cardNumber": "12345",
                    "amount": 50000,
                    "paymentMethod": "NEQUI"
                }
                """;

        mockMvc.perform(post("/api/v1/recharges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Card number must contain exactly 16 digits"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldGetRecharges() throws Exception {
        RechargeResponse recharge = new RechargeResponse(
                1L,
                "1234567890123456",
                new BigDecimal("50000"),
                PaymentMethod.NEQUI,
                LocalDateTime.now());

        when(rechargeService.getRecharges(any(), any()))
                .thenReturn(new PageImpl<>(List.of(recharge)));

        mockMvc.perform(get("/api/v1/getRecharges")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.content[0].amount").value(50000))
                .andExpect(jsonPath("$.content[0].paymentMethod").value("NEQUI"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldGetRechargesByCardNumber() throws Exception {
        RechargeResponse recharge = new RechargeResponse(
                1L,
                "1234567890123456",
                new BigDecimal("50000"),
                PaymentMethod.NEQUI,
                LocalDateTime.now());

        when(rechargeService.getRecharges(
                any(),
                any())).thenReturn(new PageImpl<>(List.of(recharge)));

        mockMvc.perform(
                get("/api/v1/getRecharges")
                        .param("cardNumber", "1234567890123456")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumber")
                        .value("1234567890123456"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeExceedsLimit() throws Exception {
        mockMvc.perform(
                get("/api/v1/getRecharges")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Size must not exceed 100"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldDeleteRecharge() throws Exception {
        Long rechargeId = 1L;

        doNothing()
                .when(rechargeService)
                .deleteRecharge(rechargeId);

        mockMvc.perform(
                delete("/api/v1/recharges/{id}", rechargeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingRecharge() throws Exception {
        Long rechargeId = 999L;

        doThrow(new RechargeNotFoundException(rechargeId))
                .when(rechargeService)
                .deleteRecharge(rechargeId);

        mockMvc.perform(
                delete("/api/v1/recharges/{id}", rechargeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Recharge not found with id: 999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenPaymentMethodIsInvalid() throws Exception {
        String requestBody = """
                {
                    "cardNumber": "1234567890123456",
                    "amount": 50000,
                    "paymentMethod": "PAYPAL"
                }
                """;

        mockMvc.perform(
                post("/api/v1/recharges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsBelowMinimum() throws Exception {
        String requestBody = """
                {
                    "cardNumber": "1234567890123456",
                    "amount": 1000,
                    "paymentMethod": "NEQUI"
                }
                """;

        mockMvc.perform(
                post("/api/v1/recharges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Amount must be at least 2000"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}