package com.tullave.recharge.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tullave.recharge.dto.RechargeRequest;
import com.tullave.recharge.dto.RechargeResponse;
import com.tullave.recharge.service.RechargeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping("/api/v1")
public class RechargeController {
    
    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @Operation(
        summary = "Create a recharge",
        description = "Creates a new recharge for a TuLlave card."
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Recharge created successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid recharge data"
        )
    })
    @PostMapping("/recharges")
    public ResponseEntity<RechargeResponse> createRecharge(
        @Valid @RequestBody RechargeRequest request
    ) {
        RechargeResponse response = rechargeService.createRecharge(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Get recharges",
        description = "Returns paginated recharges with an optional card number filter."
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Recharges retrieved successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid query parameters"
        )
    })
    @GetMapping("/getRecharges")
    public ResponseEntity<Page<RechargeResponse>> getRecharges(
        @RequestParam(required = false) 
        @Parameter(
            description = "16-digit TuLlave card number used to filter recharges",
            example = "1234567890123456"
        )
         @Pattern(
                regexp = "\\d{16}",
                message = "Card number must contain exactly 16 digits"
        )
         String cardNumber,
        
        @RequestParam(defaultValue = "0")
        @Parameter(
            description = "Zero-based page number",
            example = "0"
        )
        @Min(value = 0, message = "Page must be greater than or equal to 0")
        int page,

        @RequestParam(defaultValue = "10")
        @Parameter(
            description = "Number of records per page. Maximum value is 100",
            example = "10"
        )
        @Min(value = 1, message = "Size must be greater than 0")
        @Max(value = 100, message = "Size must not exceed 100")
        int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RechargeResponse> response = rechargeService.getRecharges(cardNumber, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a recharge",
        description = "Deletes an existing recharge by its ID."
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "204",
                description = "Recharge deleted successfully"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Recharge not found"
        )
    })
    @DeleteMapping("/recharges/{id}")
    public ResponseEntity<Void> deleteRecharge(
        @Parameter(
            description = "Unique identifier of the recharge",
            example = "1"
        )
        @PathVariable Long id
    ) {
        rechargeService.deleteRecharge(id);

        return ResponseEntity.noContent().build();
    }
}
