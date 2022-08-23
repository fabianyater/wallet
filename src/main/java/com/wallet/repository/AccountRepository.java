package com.wallet.repository;

import com.wallet.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query(value = "select a.account_balance from accounts a where a.account_id = :id", nativeQuery = true)
    Double findAccountBalance(@Param("id") Integer id);

    Account findByAccountIdAndUser_UserId(Integer accountId, Integer userId);

    List<Account> findByUser_UserId(Integer userId);
}
