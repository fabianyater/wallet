package com.wallet.service;

import com.wallet.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public List<User> get() throws Exception;

    Optional<User> getById(Integer id) throws Exception;

    public User save(User users) throws Exception;

    public boolean delete(Integer id) throws Exception;

}