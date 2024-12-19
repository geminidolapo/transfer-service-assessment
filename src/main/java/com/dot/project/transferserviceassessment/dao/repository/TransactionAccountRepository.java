package com.dot.project.transferserviceassessment.dao.repository;

import com.dot.project.transferserviceassessment.constant.AccountStatusEnum;
import com.dot.project.transferserviceassessment.dao.entity.TransactionAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransactionAccountRepository extends JpaRepository<TransactionAccount, Long> {
  Optional<TransactionAccount> getTransactionAccountByAccountNumberAndAccountStatusIs(String accountName, AccountStatusEnum accountStatus);
}