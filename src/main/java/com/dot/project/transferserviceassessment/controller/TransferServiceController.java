package com.dot.project.transferserviceassessment.controller;

import com.dot.project.transferserviceassessment.dto.request.TransactionReq;
import com.dot.project.transferserviceassessment.dto.response.ApiResponse;
import com.dot.project.transferserviceassessment.dto.response.TransactionRes;
import com.dot.project.transferserviceassessment.dto.response.TransactionSummaryRes;
import com.dot.project.transferserviceassessment.service.TransactionService;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransferServiceController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionRes>> transfer(@Valid @RequestBody TransactionReq transaction) {
        log.info("Transfer request received: {}", transaction);

        final var processedTransaction = transactionService.processTransfer(transaction);
        return ResponseEntity.ok(processedTransaction);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionRes>>> getTransactions(
            @Pattern(
                    regexp = "^(SUCCESSFUL|INSUFFICIENT_FUND|FAILED)$",
                    message = "Invalid status. Allowed values: SUCCESSFUL, INSUFFICIENT_FUND, FAILED"
            )
            @RequestParam(required = false) String status,

            @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters!")
            @RequestParam(required = false) String sourceAccountNumber,

            @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters!")
            @RequestParam(required = false) String destinationAccountNumber,

            @RequestParam(required = false)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            String startDate,

            @RequestParam(required = false)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            String endDate,

            Pageable pageable) {
        log.info("Transaction search request: status={}, sourceAccountNumber={}, destinationAccountNumber={}, startDate={}," +
                        " endDate={}", status, sourceAccountNumber, destinationAccountNumber, startDate, endDate);

        final var transactions = transactionService.getTransactions(
                status, sourceAccountNumber, destinationAccountNumber, startDate, endDate, pageable);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<TransactionSummaryRes>> getDailySummary(
            @RequestParam(required = false)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();

        log.info("Daily summary request: date={}", date);

        final var summary = transactionService.getDailySummary(date);
        return ResponseEntity.ok(summary);
    }
}

