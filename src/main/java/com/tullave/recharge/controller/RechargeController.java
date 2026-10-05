package com.tullave.recharge.controller;

import com.tullave.recharge.dto.RechargeRequest;
import com.tullave.recharge.dto.RechargeResponse;
import com.tullave.recharge.service.RechargeService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@Validated
@RequestMapping("/api/v1")
public class RechargeController {
    
    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @PostMapping("/recharges")
    public ResponseEntity<RechargeResponse> createRecharge(
        @Valid @RequestBody RechargeRequest request
    ) {
        RechargeResponse response = rechargeService.createRecharge(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getRecharges")
    public ResponseEntity<Page<RechargeResponse>> getRecharges(
         @RequestParam(required = false) 
         @Pattern(
                regexp = "\\d{16}",
                message = "Card number must contain exactly 16 digits"
        )
         String cardNumber,
        
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "Page must be greater than or equal to 0")
        int page,

        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "Size must be greater than 0")
        @Max(value = 100, message = "Size must not exceed 100")
        int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RechargeResponse> response = rechargeService.getRecharges(cardNumber, pageable);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/recharges/{id}")
    public ResponseEntity<Void> deleteRecharge(@PathVariable Long id) {
        rechargeService.deleteRecharge(id);

        return ResponseEntity.noContent().build();
    }
}
