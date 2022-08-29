package com.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wallet.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
