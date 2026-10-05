package com.tullave.recharge.exception;

public class RechargeNotFoundException extends RuntimeException {
    
    public RechargeNotFoundException(Long id) {
        super("Recharge not found with id: " + id);
    }
}
