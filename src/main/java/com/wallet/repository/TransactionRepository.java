package com.wallet.repository;

import com.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccountId_AccountId(Integer accountId);

    Optional<Transaction> findByTransactionIdAndAccountId_AccountId(Integer transactionId, Integer accountId);
}
