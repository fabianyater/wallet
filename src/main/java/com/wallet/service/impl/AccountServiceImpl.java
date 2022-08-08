package com.wallet.service.impl;

import com.wallet.entity.Account;
import com.wallet.repository.AccountRepository;
import com.wallet.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<Account> getAccounts() throws Exception {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(int id) throws Exception {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account createAccount(Account account) throws Exception {
        return accountRepository.save(account);
    }
}
