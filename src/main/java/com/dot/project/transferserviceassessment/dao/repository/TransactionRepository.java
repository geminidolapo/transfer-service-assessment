package com.dot.project.transferserviceassessment.dao.repository;

import com.dot.project.transferserviceassessment.constant.StatusEnum;
import com.dot.project.transferserviceassessment.dao.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> , JpaSpecificationExecutor<Transaction> {
    @Override
    List<Transaction> findAll(Specification<Transaction> spec);
    List<Transaction> findByStatusAndCreatedAtBetween(StatusEnum status, LocalDateTime start, LocalDateTime end);
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}