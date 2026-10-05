package com.tullave.recharge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(
        MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();

        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            message
        );
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception exception) {

        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An internal error has occurred"
        );
    }
}
