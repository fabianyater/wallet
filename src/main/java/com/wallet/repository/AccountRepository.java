package com.wallet.repository;

import com.wallet.entity.Account;
import com.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query(value = "select a.account_balance from accounts a where a.account_id = :id", nativeQuery = true)
    Double findAccountBalance(@Param("id") Integer id);

    @Query(value = "select SUM(t.transaction_amount) from transactions t inner join accounts a \n" +
            "on t.account_id_account_id = a.account_id where a.account_id = :accountId and t.transaction_type = 'withdraw'\n" +
            "and t.transaction_category <> 'tax'", nativeQuery = true)
    Double getTransactionAmountsByAccountId(@Param("accountId") Integer accountId);

    @Query(value = "select t from transactions t inner join accounts a on t.account_id_account_id = a.account_id \n" +
            " where a.account_id = :accountId", nativeQuery = true)
    List<Transaction> getAccountTransactions(@Param("accountId") Integer accountId);
}
