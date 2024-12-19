package com.dot.project.transferserviceassessment.scheduler;

import com.dot.project.transferserviceassessment.config.ExternalRequestProperties;
import com.dot.project.transferserviceassessment.constant.StatusEnum;
import com.dot.project.transferserviceassessment.dao.entity.Transaction;
import com.dot.project.transferserviceassessment.dao.repository.TransactionRepository;
import com.dot.project.transferserviceassessment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TransactionRepository transactionRepository;
    private final ExternalRequestProperties properties;
    private final TransactionService transactionService;
    public static final ZoneId ZONE_ID = ZoneId.of("Africa/Lagos");

    @Scheduled(cron = "0 0 1 * * ?") // Daily at 1 AM
    public void processCommissions() {
        log.info("Starting commission processing task...");

        LocalDateTime yesterdayStartOfDayMidnight = getYesterdayStartOfDay();
        LocalDateTime yesterdayEndOfDay = getYesterdayEndOfDay();
        log.info("Processing commissions for transactions created between: {} and {}",
                yesterdayStartOfDayMidnight, yesterdayEndOfDay);

        List<Transaction> successfulTransactions = transactionRepository.findByStatusAndCreatedAtBetween(
                StatusEnum.SUCCESSFUL, yesterdayStartOfDayMidnight, yesterdayEndOfDay);

        log.info("Found {} successful transactions for commission processing.", successfulTransactions.size());

        for (Transaction transaction : successfulTransactions) {
            try {
                transaction.setCommissionWorthy(true);
                BigDecimal transactionFee = transaction.getFee();
                transaction.setCommission(transactionFee.multiply(new BigDecimal(properties.getCommissionPercentage()))); // 20% of transaction fee
                transactionRepository.save(transaction);
                log.info("Commission processed for transaction ID: {}", transaction.getId());
            } catch (Exception e) {
                log.error("Error processing commission for transaction ID: {} , {}", transaction.getId(), e.getMessage());
            }
        }
    }


    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void generateDailySummary() {
        log.info("Starting daily summary generation task...");

        LocalDateTime yesterdayStartOfDayMidnight = getYesterdayStartOfDay();
        LocalDateTime yesterdayEndOfDay = getYesterdayEndOfDay();
        log.info("Generating daily summary for transactions created between: {} and {}",
                yesterdayStartOfDayMidnight, yesterdayEndOfDay);

        final var summary = transactionService.getTransactionSummary(yesterdayStartOfDayMidnight,yesterdayEndOfDay);

        // TODO: send email

        log.info("Daily summary generated successfully for date: {}", yesterdayStartOfDayMidnight);
    }

    private LocalDateTime getYesterdayStartOfDay() {
        return LocalDateTime.now(ZONE_ID).with(LocalTime.MIDNIGHT).minusDays(1);
    }

    private LocalDateTime getYesterdayEndOfDay() {
        return LocalDateTime.now(ZONE_ID).with(LocalTime.of(23, 59)).minusDays(1);
    }
}
