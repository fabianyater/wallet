package com.wallet.repository;

import com.wallet.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccount_AccountId(Integer accountId);


    Optional<Transaction> findByTransactionIdAndAccount_AccountId(Integer transactionId, Integer accountId);

    @Query(value = "select SUM(t.amount) from transactions t inner join accounts a \n" +
            "on t.account_id = a.id where a.id = :accountId and t.type = 'expense'\n" +
            "and t.category <> 'tax'", nativeQuery = true)
    Double getTransactionAmountsByAccountId(@Param("accountId") Integer accountId);
}
