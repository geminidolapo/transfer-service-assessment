package com.dot.project.transferserviceassessment;

import com.dot.project.transferserviceassessment.config.ExternalRequestProperties;
import com.dot.project.transferserviceassessment.constant.CurrencyEnum;
import com.dot.project.transferserviceassessment.constant.StatusEnum;
import com.dot.project.transferserviceassessment.controller.TransferServiceController;
import com.dot.project.transferserviceassessment.dao.entity.Transaction;
import com.dot.project.transferserviceassessment.dao.repository.TransactionRepository;
import com.dot.project.transferserviceassessment.dto.request.TransactionReq;
import com.dot.project.transferserviceassessment.dto.response.ApiResponse;
import com.dot.project.transferserviceassessment.dto.response.TransactionRes;
import com.dot.project.transferserviceassessment.dto.response.TransactionSummaryRes;
import com.dot.project.transferserviceassessment.scheduler.ScheduledTasks;
import com.dot.project.transferserviceassessment.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class TransferServiceAssessmentApplicationTests {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransferServiceController transferServiceController;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExternalRequestProperties properties;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnSuccessfulResponseWhenValidTransactionRequestIsProcessed() {
        // Arrange: Set up the request object with valid data
        TransactionReq transactionReq = TransactionReq.builder()
                .reference("test transfer")
                .amount(new BigDecimal("5.00"))
                .currency(CurrencyEnum.USD)
                .sourceAccountNumber("1234567890")
                .destinationAccountNumber("2113182084")
                .build();

        // Arrange: Set up the expected response
        TransactionRes transactionRes = TransactionRes.builder()
                .reference("test transfer")
                .amount(new BigDecimal("5.00"))
                .fee(new BigDecimal("0.02500"))
                .billedAmount(new BigDecimal("5.02500"))
                .createdAt(LocalDateTime.now())
                .status(StatusEnum.SUCCESSFUL)
                .statusMessage("Transaction Successful")
                .sourceAccountNumber("1234567890")
                .destinationAccountNumber("2113182084")
                .build();
        ApiResponse<TransactionRes> expectedResponse = ApiResponse.success(transactionRes);

        // Mock the service call
        when(transactionService.processTransfer(transactionReq)).thenReturn(expectedResponse);

        // Act: Call the controller method
        ResponseEntity<ApiResponse<TransactionRes>> response = transferServiceController.transfer(transactionReq);

        // Assert: Validate the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        log.info("response {}", response);

        assertEquals(expectedResponse.getData().getStatus(), Objects.requireNonNull(response.getBody()).getData().getStatus());
    }


    @Test
    void shouldCorrectlyBuildSpecificationWhenAllFiltersAreProvidedAndMatchTransactions() {
        // Arrange
        String status = "SUCCESSFUL";
        String sourceAccountNumber = "1234567890";
        String destinationAccountNumber = "2113182085";
        Pageable pageable = PageRequest.of(0, 1);

        // Create a mock transaction
        Transaction test_transaction = Transaction.builder()
                .amount(new BigDecimal("5.00"))
                .billedAmount(new BigDecimal("5.03"))
                .createdAt(LocalDateTime.parse("2024-12-18 22:59:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .destinationAccountNumber(destinationAccountNumber)
                .fee(new BigDecimal("0.03"))
                .reference("test transfer")
                .sourceAccountNumber(sourceAccountNumber)
                .status(StatusEnum.SUCCESSFUL)
                .statusMessage("Transaction Successful")
                .build();

        List<Transaction> transactions = List.of(test_transaction);
        Page<Transaction> pagedResults = new PageImpl<>(transactions, pageable, transactions.size());

        // Mock repository behavior
        when(transactionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagedResults);

        // Act
        ResponseEntity<ApiResponse<Page<TransactionRes>>> response = transferServiceController.getTransactions(
                status, sourceAccountNumber, destinationAccountNumber, null, null, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.is2xxSuccessful(), response.getStatusCode().is2xxSuccessful());
    }
    
    
    
    @Test
    void shouldReturnApiResponseWithSuccessStatusWhenGetDailySummaryIsCalledWithValidLocalDate() {
        // Arrange
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59);
        TransactionSummaryRes summaryRes = new TransactionSummaryRes(startOfDay, endOfDay, 10, 8, 2, new BigDecimal("100.00"), new BigDecimal("5.00"));

        when(transactionRepository.findByCreatedAtBetween(startOfDay, endOfDay)).thenReturn(List.of(
                Transaction.builder().amount(new BigDecimal("10.00")).status(StatusEnum.SUCCESSFUL).commissionWorthy(true).commission(new BigDecimal("0.50")).build(),
                Transaction.builder().amount(new BigDecimal("90.00")).status(StatusEnum.SUCCESSFUL).commissionWorthy(true).commission(new BigDecimal("4.50")).build()
        ));

        // Act
        ResponseEntity<ApiResponse<TransactionSummaryRes>> response = transferServiceController.getDailySummary(date);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.is2xxSuccessful(), response.getStatusCode().is2xxSuccessful());
    }
}
