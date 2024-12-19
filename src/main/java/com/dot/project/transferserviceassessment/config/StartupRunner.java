package com.dot.project.transferserviceassessment.config;

import com.dot.project.transferserviceassessment.constant.AccountStatusEnum;
import com.dot.project.transferserviceassessment.constant.CurrencyEnum;
import com.dot.project.transferserviceassessment.constant.StatusEnum;
import com.dot.project.transferserviceassessment.dao.entity.Transaction;
import com.dot.project.transferserviceassessment.dao.entity.TransactionAccount;
import com.dot.project.transferserviceassessment.dao.repository.TransactionAccountRepository;
import com.dot.project.transferserviceassessment.dao.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static com.dot.project.transferserviceassessment.scheduler.ScheduledTasks.ZONE_ID;


@Slf4j
@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private TransactionAccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize and save the default source account
        TransactionAccount sourceAccount = TransactionAccount.builder()
                .accountNumber("1234567890")
                .accountName("Test Source Account")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .balance(BigDecimal.TEN) // Balance = 10
                .currency(CurrencyEnum.USD)
                .build();

        // Initialize and save the default destination account
        TransactionAccount destinationAccount = TransactionAccount.builder()
                .accountNumber("2113182084")
                .accountName("Test Destination Account")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .balance(BigDecimal.TEN)
                .currency(CurrencyEnum.USD)
                .build();

        TransactionAccount destinationAccount_2 = TransactionAccount.builder()
                .accountNumber("2113182085")
                .accountName("Test Destination Account")
                .accountStatus(AccountStatusEnum.ACTIVE)
                .balance(BigDecimal.TEN)
                .currency(CurrencyEnum.USD)
                .build();

        if (accountRepository.count() == 0) {
            accountRepository.saveAll(List.of(sourceAccount,destinationAccount, destinationAccount_2));
            log.info("Default accounts initialized in the database.");
        } else {
            log.info("Accounts already exist in the database.");
        }

        if(transactionRepository.count() == 0){
            Transaction test_transaction = Transaction.builder()
                    .amount(new BigDecimal("5.00"))
                    .billedAmount(new BigDecimal("5.03"))
                    .createdAt(LocalDateTime.parse("2024-12-18 22:59:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .destinationAccountNumber("2113182085")
                    .fee(new BigDecimal("0.03"))
                    .reference("test transfer")
                    .sourceAccountNumber("1234567890")
                    .status(StatusEnum.SUCCESSFUL)
                    .statusMessage("Transaction Successful")
                    .build();

            transactionRepository.save(test_transaction);
        }

        Transaction test_transaction_1 = Transaction.builder()
                .amount(new BigDecimal("5.00"))
                .billedAmount(new BigDecimal("5.03"))
                .createdAt(LocalDateTime.now(ZONE_ID).with(LocalTime.of(22, 59)).minusDays(1))
                .destinationAccountNumber("2113182084")
                .fee(new BigDecimal("0.03"))
                .reference("test transfer on " + LocalDateTime.now(ZONE_ID))
                .sourceAccountNumber("1234567890")
                .status(StatusEnum.SUCCESSFUL)
                .statusMessage("Transaction Successful")
                .build();

        Transaction test_transaction_2 = Transaction.builder()
                .amount(new BigDecimal("5.00"))
                .billedAmount(new BigDecimal("5.03"))
                .createdAt(LocalDateTime.now(ZONE_ID).with(LocalTime.of(22, 59)).minusDays(1))
                .destinationAccountNumber("2113182084")
                .fee(new BigDecimal("0.03"))
                .reference("test transfer on " + LocalDateTime.now(ZONE_ID))
                .sourceAccountNumber("1234567890")
                .status(StatusEnum.SUCCESSFUL)
                .statusMessage("Transaction Successful")
                .build();

        Transaction test_transaction_3 = Transaction.builder()
                .amount(new BigDecimal("5.00"))
                .billedAmount(new BigDecimal("5.03"))
                .createdAt(LocalDateTime.now(ZONE_ID))
                .destinationAccountNumber("2113182084")
                .fee(new BigDecimal("0.03"))
                .reference("test transfer on " + LocalDateTime.now(ZONE_ID))
                .sourceAccountNumber("1234567890")
                .status(StatusEnum.SUCCESSFUL)
                .statusMessage("Transaction Successful")
                .build();

        transactionRepository.saveAll(List.of(test_transaction_1, test_transaction_2, test_transaction_3));

        log.info("Default transactions initialized in the database.");
    }
}