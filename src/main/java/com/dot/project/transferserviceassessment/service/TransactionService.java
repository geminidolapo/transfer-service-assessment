package com.dot.project.transferserviceassessment.service;

import com.dot.project.transferserviceassessment.config.ExternalRequestProperties;
import com.dot.project.transferserviceassessment.constant.StatusEnum;
import com.dot.project.transferserviceassessment.dao.entity.Transaction;
import com.dot.project.transferserviceassessment.dao.repository.TransactionRepository;
import com.dot.project.transferserviceassessment.dto.request.TransactionReq;
import com.dot.project.transferserviceassessment.dto.response.ApiResponse;
import com.dot.project.transferserviceassessment.dto.response.TransactionRes;
import com.dot.project.transferserviceassessment.dto.response.TransactionSummaryRes;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final ExternalRequestProperties properties;


    /**
     * Processes a transfer transaction between two accounts.
     *
     * @param transactionReq the transaction request containing details such as source account number,
     *                       destination account number, reference, amount, and currency.
     * @return an ApiResponse containing a TransactionRes object if the transaction is successful,
     *         or an error message if the transaction fails.
     */
    public ApiResponse<TransactionRes> processTransfer(TransactionReq transactionReq) {
        log.info("Starting transfer process: Source Account = {}, Destination Account = {}, Reference = {}",
                transactionReq.getSourceAccountNumber(), transactionReq.getDestinationAccountNumber(), transactionReq.getReference());

        // Fetch source and destination account details
        final var sourceAccount = accountService.accountEnquiry(transactionReq.getSourceAccountNumber());
        final var destinationAccount = accountService.accountEnquiry(transactionReq.getDestinationAccountNumber());

        log.info("Source Account: {}, Balance: {}, Currency: {}",
                sourceAccount.getAccountNumber(), sourceAccount.getBalance(), sourceAccount.getCurrency());
        log.info("Destination Account: {}, Balance: {}, Currency: {}",
                destinationAccount.getAccountNumber(), destinationAccount.getBalance(), destinationAccount.getCurrency());

        // Build the transaction object
        final var transaction = buildTransaction(transactionReq);
        log.info("Transaction initialized: {}", transaction);

        // Check for currency mismatches
        if (!sourceAccount.getCurrency().equals(transactionReq.getCurrency())) {
            String errorMessage = String.format("Currency mismatch for Source Account: %s, Expected: %s, Provided: %s",
                    sourceAccount.getAccountNumber(), sourceAccount.getCurrency(), transactionReq.getCurrency());
            log.warn(errorMessage);
            return handleTransactionFailure(transaction, StatusEnum.FAILED, errorMessage);
        }

        if (!destinationAccount.getCurrency().equals(transactionReq.getCurrency())) {
            String errorMessage = String.format("Currency mismatch for Destination Account: %s, Expected: %s, Provided: %s",
                    destinationAccount.getAccountNumber(), destinationAccount.getCurrency(), transactionReq.getCurrency());
            log.warn(errorMessage);
            return handleTransactionFailure(transaction, StatusEnum.FAILED, errorMessage);
        }

        // Check for insufficient funds
        if (sourceAccount.getBalance().compareTo(transaction.getBilledAmount()) < 0) {
            String errorMessage = String.format("Insufficient funds for Source Account: %s, Balance: %s, Requested: %s",
                    sourceAccount.getAccountNumber(), sourceAccount.getBalance(), transaction.getBilledAmount());
            log.warn(errorMessage);
            return handleTransactionFailure(transaction, StatusEnum.INSUFFICIENT_FUND, errorMessage);
        }

        // Perform the transfer
        try {
            log.info("Debiting Source Account: {}, Amount: {}", sourceAccount.getAccountNumber(), transactionReq.getAmount());
            accountService.debitAccount(sourceAccount, transaction.getBilledAmount());

            log.info("Crediting Destination Account: {}, Amount: {}", destinationAccount.getAccountNumber(), transaction.getAmount());
            accountService.creditAccount(destinationAccount, transaction.getAmount());

            transaction.setStatus(StatusEnum.SUCCESSFUL);
            transaction.setStatusMessage("Transaction Successful");
            transactionRepository.save(transaction);

            log.info("Transaction completed successfully: Source Account = {}, Reference = {}",
                    sourceAccount.getAccountNumber(), transactionReq.getReference());
            return ApiResponse.success(new TransactionRes(transaction));

        } catch (Exception e) {
            log.error("Error during transfer process for Reference: {}", transactionReq.getReference(), e);
            return handleTransactionFailure(transaction, StatusEnum.FAILED, "An error occurred during transaction processing");
        }
    }


    private Transaction buildTransaction(TransactionReq transactionReq) {
        final var fee = calculateFee(transactionReq.getAmount());
        return Transaction.builder()
                .sourceAccountNumber(transactionReq.getSourceAccountNumber())
                .destinationAccountNumber(transactionReq.getDestinationAccountNumber())
                .reference(transactionReq.getReference())
                .amount(transactionReq.getAmount())
                .fee(fee)
                .billedAmount(transactionReq.getAmount().add(fee))
                .description(transactionReq.getDescription())
                .build();
    }

    private BigDecimal calculateFee(BigDecimal transactionAmount) {
        BigDecimal fee = transactionAmount.multiply(new BigDecimal(properties.getFeePercentage())); // Percentage of amount
        return fee.min(new BigDecimal(properties.getFeeCap())); // Apply cap
    }

    private ApiResponse<TransactionRes> handleTransactionFailure(Transaction transaction, StatusEnum status, String statusMessage) {
        log.error("Transaction failed. Status: {}, Reason: {}", status, statusMessage);
        transaction.setStatus(status);
        transaction.setStatusMessage(statusMessage);
        transactionRepository.save(transaction);
        log.info("Transaction saved with failure status: {}", transaction);

        return ApiResponse.failed(new TransactionRes(transaction));
    }


    /**
     * Retrieves a paginated list of transactions based on the provided filters.
     *
     * @param status               the status of the transactions to filter by (e.g., SUCCESSFUL, FAILED).
     * @param sourceAccountNumber  the source account number to filter transactions by.
     * @param destinationAccountNumber the destination account number to filter transactions by.
     * @param startDate            the start date for the transaction creation date filter, in "yyyy-MM-dd HH:mm:ss" format.
     * @param endDate              the end date for the transaction creation date filter, in "yyyy-MM-dd HH:mm:ss" format.
     * @param pageable             the pagination information, including page number and size.
     * @return an ApiResponse containing a Page of TransactionRes objects that match the specified filters.
     */
    public ApiResponse<Page<TransactionRes>> getTransactions(String status, String sourceAccountNumber,
                                                             String destinationAccountNumber, String startDate,
                                                             String endDate, Pageable pageable) {

        Specification<Transaction> specification = Specification.where(null);

        // Build the specification dynamically based on input filters
        if (StringUtils.isNotBlank(status)) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (StringUtils.isNotBlank(sourceAccountNumber)) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("sourceAccountNumber"), sourceAccountNumber));
        }

        if (StringUtils.isNotBlank(destinationAccountNumber)) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("destinationAccountNumber"), destinationAccountNumber));
        }

        if (startDate != null && endDate != null) {
            specification = specification.and((root, query, cb) -> cb.between(root.get("createdAt"), startDate, endDate));
        } else if (startDate != null) {
            LocalDateTime finalStartDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), finalStartDate));
        } else if (endDate != null) {
            LocalDateTime finalEndDate = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), finalEndDate));
        }

        // Fetch paginated results
        Page<Transaction> pagedResults = transactionRepository.findAll(specification, pageable);

        if (pagedResults.isEmpty()) {
            log.info("No transactions found for the given filters: Status={}, SourceAccount={}, DestinationAccount={}, StartDate={}, EndDate={}",
                    status, sourceAccountNumber, destinationAccountNumber, startDate, endDate);
        }

        // Map entities to DTOs
        List<TransactionRes> transactionResponses = pagedResults.getContent()
                .stream()
                .map(TransactionRes::new)
                .toList();

        log.info("Fetched {} transactions for the given filters.", transactionResponses.size());

        // Return paginated response
        return ApiResponse.success(new PageImpl<>(transactionResponses, pageable, pagedResults.getTotalElements()));
    }


    public ApiResponse<TransactionSummaryRes> getDailySummary(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23,59);
        return ApiResponse.success(getTransactionSummary(startOfDay,endOfDay));
    }

    /**
     * Generates a summary of transactions for a specified time period.
     * This method fetches all transactions within the given time range and calculates
     * various metrics such as total transactions, total amount, total commission,
     * and counts of successful and failed transactions.
     *
     * @param start The start date and time of the period for which to generate the summary.
     * @param end The end date and time of the period for which to generate the summary.
     * @return A TransactionSummaryRes object containing the calculated metrics for the specified period.
     *         This includes the total number of transactions, counts of successful and failed transactions,
     *         total transaction amount, and total commission earned.
     */
    public TransactionSummaryRes getTransactionSummary(LocalDateTime start, LocalDateTime end) {
        log.info("Starting transaction summary for period: {} to {}", start, end);

        // Fetch all transactions for the specified day
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(start, end);
        log.info("Fetched {} transactions for the specified period.", transactions.size());

        // Aggregate metrics
        int totalTransactions = transactions.size();
        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Calculated total amount: {}", totalAmount);

        BigDecimal totalCommission = transactions.stream()
                .filter(transaction -> Boolean.TRUE.equals(transaction.getCommissionWorthy()))
                .map(Transaction::getCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Calculated total commission: {}", totalCommission);

        long successfulTransactions = transactions.stream()
                .filter(transaction -> StatusEnum.SUCCESSFUL.equals(transaction.getStatus()))
                .count();
        log.info("Counted {} successful transactions.", successfulTransactions);

        long failedTransactions = transactions.stream()
                .filter(transaction -> StatusEnum.FAILED.equals(transaction.getStatus()))
                .count();
        log.info("Counted {} failed transactions.", failedTransactions);

        log.info("Transaction summary completed for period: {} to {}", start, end);

        return new TransactionSummaryRes(start, end, totalTransactions, successfulTransactions,
                failedTransactions, totalAmount, totalCommission);
    }
}