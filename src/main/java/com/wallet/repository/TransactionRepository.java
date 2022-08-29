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
    @Query(value = "select SUM(t.transaction_amount) from transactions t inner join accounts a \n" +
            "on t.account_id_account_id = a.account_id where a.account_id = :accountId and t.transaction_type = 'withdraw'\n" +
            "and t.transaction_category <> 'tax'", nativeQuery = true)
    Double getTransactionAmountsByAccountId(@Param("accountId") Integer accountId);
}
