package com.tullave.recharge.exception;

public record ErrorResponse (
    int status,
    String message
) {}

