package com.company.midas.repository;

import com.company.midas.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByExternalTxId(String externalTxId);
}
