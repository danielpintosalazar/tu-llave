package com.tullave.recharge.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.tullave.recharge.dto.RechargeRequest;
import com.tullave.recharge.dto.RechargeResponse;
import com.tullave.recharge.entity.Recharge;
import com.tullave.recharge.enums.PaymentMethod;
import com.tullave.recharge.exception.RechargeNotFoundException;
import com.tullave.recharge.repository.RechargeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RechargeServiceTest {

    @Mock
    private RechargeRepository rechargeRepository;

    @InjectMocks
    private RechargeService rechargeService;

    @Test
    void shouldCreateRecharge() {
        RechargeRequest request = new RechargeRequest(
                "1234567890123456",
                new BigDecimal("50000"),
                PaymentMethod.NEQUI
        );

        Recharge savedRecharge = new Recharge();
        savedRecharge.setCardNumber(request.cardNumber());
        savedRecharge.setAmount(request.amount());
        savedRecharge.setPaymentMethod(request.paymentMethod());

        when(rechargeRepository.save(any(Recharge.class)))
                .thenReturn(savedRecharge);

        RechargeResponse response = rechargeService.createRecharge(request);

        assertEquals(request.cardNumber(), response.cardNumber());
        assertEquals(request.amount(), response.amount());
        assertEquals(request.paymentMethod(), response.paymentMethod());
    }

    @Test
    void shouldGetRechargesWithoutCardFilter() {
        PageRequest pageable = PageRequest.of(0, 10);

        Recharge recharge = new Recharge();
        recharge.setCardNumber("1234567890123456");
        recharge.setAmount(new BigDecimal("50000"));
        recharge.setPaymentMethod(PaymentMethod.NEQUI);

        Page<Recharge> rechargePage = new PageImpl<>(List.of(recharge), pageable, 1);

        when(rechargeRepository.findAll(pageable))
                .thenReturn(rechargePage);

        Page<RechargeResponse> response = rechargeService.getRecharges(null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("1234567890123456", response.getContent().get(0).cardNumber());

        verify(rechargeRepository).findAll(pageable);
    }

    @Test
    void shouldGetRechargesByCardNumber() {
        PageRequest pageable = PageRequest.of(0, 10);
        String cardNumber = "1234567890123456";

        Recharge recharge = new Recharge();
        recharge.setCardNumber(cardNumber);
        recharge.setAmount(new BigDecimal("50000"));
        recharge.setPaymentMethod(PaymentMethod.NEQUI);

        Page<Recharge> rechargePage = new PageImpl<>(List.of(recharge), pageable, 1);

        when(rechargeRepository.findByCardNumber(cardNumber, pageable))
                .thenReturn(rechargePage);

        Page<RechargeResponse> response = rechargeService.getRecharges(cardNumber, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(cardNumber, response.getContent().get(0).cardNumber());

        verify(rechargeRepository).findByCardNumber(cardNumber, pageable);
    }

    @Test
    void shouldDeleteRecharge() {
        Long rechargeId = 1L;

        Recharge recharge = new Recharge();

        when(rechargeRepository.findById(rechargeId))
                .thenReturn(Optional.of(recharge));

        rechargeService.deleteRecharge(rechargeId);

        verify(rechargeRepository).findById(rechargeId);
        verify(rechargeRepository).delete(recharge);
    }

    @Test
    void shouldThrowExceptionWhenRechargeDoesNotExist() {
        Long rechargeId = 999L;

        when(rechargeRepository.findById(rechargeId))
                .thenReturn(Optional.empty());

        assertThrows(
                RechargeNotFoundException.class,
                () -> rechargeService.deleteRecharge(rechargeId)
        );

        verify(rechargeRepository).findById(rechargeId);
        verify(rechargeRepository, never()).delete(any(Recharge.class));
    }
}