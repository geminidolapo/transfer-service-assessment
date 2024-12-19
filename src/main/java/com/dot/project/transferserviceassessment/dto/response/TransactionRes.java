package com.dot.project.transferserviceassessment.dto.response;

import com.dot.project.transferserviceassessment.constant.CurrencyEnum;
import com.dot.project.transferserviceassessment.constant.StatusEnum;
import com.dot.project.transferserviceassessment.dao.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link Transaction}
 */
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRes implements Serializable {
    private final String reference;
    private final BigDecimal amount;
    private BigDecimal fee;
    private CurrencyEnum currency;
    private BigDecimal billedAmount;
    private final String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final StatusEnum status;
    private final String statusMessage;
    private Boolean commissionWorthy;
    private BigDecimal commission;
    private String sourceAccountNumber;
    private String destinationAccountNumber;

    public TransactionRes(Transaction transaction) {
        this.reference = transaction.getReference();
        this.amount = transaction.getAmount();
        this.fee = transaction.getFee();
        this.currency = transaction.getCurrency();
        this.billedAmount = transaction.getBilledAmount();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreatedAt();
        this.status = transaction.getStatus();
        this.statusMessage = transaction.getStatusMessage();
        this.commissionWorthy = transaction.getCommissionWorthy();
        this.commission = transaction.getCommission();
        this.sourceAccountNumber = transaction.getSourceAccountNumber();
        this.destinationAccountNumber = transaction.getDestinationAccountNumber();
    }
}