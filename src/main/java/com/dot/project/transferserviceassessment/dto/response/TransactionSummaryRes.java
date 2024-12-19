package com.dot.project.transferserviceassessment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionSummaryRes {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate, endDate;
    private long totalTransactions;
    private long successfulTransactions;
    private long failedTransactions;
    private BigDecimal totalAmount;
    private BigDecimal totalCommission;
}
