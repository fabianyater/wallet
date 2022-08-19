package com.wallet.repository;

import com.wallet.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query(value = "select a.account_balance from accounts a where a.account_id = :id", nativeQuery = true)
    Double findAccountBalance(@Param("id") Integer id);
}
