package com.dot.project.transferserviceassessment.dao.entity;

import com.dot.project.transferserviceassessment.constant.AccountStatusEnum;
import com.dot.project.transferserviceassessment.constant.CurrencyEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="deleted=false")
@SQLDelete(sql="UPDATE transaction_account SET deleted=true WHERE id=?")
@Table(name = "transaction_account", indexes = @Index(name = "transaction_account_idx",
                columnList = "account_number, created_at, account_status"))
public class TransactionAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 15)
    private String accountNumber;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @Builder.Default
    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum accountStatus = AccountStatusEnum.ACTIVE;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(columnDefinition="tinyint(1) default 0")
    private boolean deleted = Boolean.FALSE;
}