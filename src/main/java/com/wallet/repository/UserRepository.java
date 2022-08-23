package com.wallet.repository;

import com.wallet.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wallet.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
