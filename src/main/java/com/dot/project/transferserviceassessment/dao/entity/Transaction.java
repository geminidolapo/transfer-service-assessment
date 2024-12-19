package com.dot.project.transferserviceassessment.dao.entity;

import com.dot.project.transferserviceassessment.constant.CurrencyEnum;
import com.dot.project.transferserviceassessment.constant.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="deleted=false")
@SQLDelete(sql="UPDATE transaction SET deleted=true WHERE id=?")
@Table(name = "transaction",
        indexes ={
                @Index(name = "transaction_idx_1", columnList = "reference, amount, created_at, status"),
                @Index(name = "transaction_idx_2", columnList = "source_account_number,destination_account_number")
        })
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal fee;

    private CurrencyEnum currency;

    @Column(name = "billed_amount")
    private BigDecimal billedAmount; // Amount + Fee

    @Column(columnDefinition = "TEXT")
    private String description;

//    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private StatusEnum status; // SUCCESSFUL, INSUFFICIENT FUND, FAILED

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "commission_worthy")
    private Boolean commissionWorthy;

    private BigDecimal commission;

    @Column(name = "source_account_number", nullable = false, length = 20)
    private String sourceAccountNumber;

    @Column(name = "destination_account_number", nullable = false, length = 20)
    private String destinationAccountNumber;

    @Builder.Default
    @Column(columnDefinition="tinyint(1) default 0")
    private boolean deleted = Boolean.FALSE;

    @PrePersist
    private void prePersist() {
        if (Objects.isNull(this.createdAt)) {
            this.setCreatedAt(
                    LocalDateTime.now(ZoneId.of("Africa/Lagos"))
            );
        }
    }
}