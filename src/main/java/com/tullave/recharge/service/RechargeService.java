package com.tullave.recharge.service;

import com.tullave.recharge.dto.RechargeRequest;
import com.tullave.recharge.dto.RechargeResponse;
import com.tullave.recharge.entity.Recharge;
import com.tullave.recharge.exception.RechargeNotFoundException;
import com.tullave.recharge.repository.RechargeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RechargeService {

    private final RechargeRepository rechargeRepository;

    public RechargeService(RechargeRepository rechargeRepository) {
        this.rechargeRepository = rechargeRepository;
    }

    public RechargeResponse createRecharge(RechargeRequest request) {
        Recharge recharge = new Recharge();

        recharge.setCardNumber(request.cardNumber());
        recharge.setAmount(request.amount());
        recharge.setPaymentMethod(request.paymentMethod());

        Recharge savedRecharge = rechargeRepository.save(recharge);

        return new RechargeResponse(
            savedRecharge.getId(),
            savedRecharge.getCardNumber(),
            savedRecharge.getAmount(),
            savedRecharge.getPaymentMethod(),
            savedRecharge.getCreatedAt()
        );
    }

    public Page<RechargeResponse> getRecharges(
        String cardNumber,
        Pageable pageable
    ) {
        Page<Recharge> recharges;

        if (cardNumber != null && !cardNumber.isBlank()) {
            recharges = rechargeRepository.findByCardNumber(cardNumber, pageable);
        } else {
            recharges = rechargeRepository.findAll(pageable);
        }

        return recharges.map(recharge -> new RechargeResponse(
            recharge.getId(),
            recharge.getCardNumber(),
            recharge.getAmount(),
            recharge.getPaymentMethod(),
            recharge.getCreatedAt()
        ));
    }

    public void deleteRecharge(Long id) {
        Recharge recharge = rechargeRepository.findById(id)
            .orElseThrow(() -> new RechargeNotFoundException(id));

        rechargeRepository.delete(recharge);
    }
}
