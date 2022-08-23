package com.wallet.service.impl;

import com.wallet.entity.Account;
import com.wallet.entity.Transaction;
import com.wallet.repository.AccountRepository;
import com.wallet.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<Account> getAccounts() throws Exception {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> getAccountsByUserId(int userId) throws Exception {
        return accountRepository.findByUser_UserId(userId);
    }

    @Override
    public Account getAccountDetails(int accountId, int userId) throws Exception {
        return accountRepository.findByAccountIdAndUser_UserId(accountId, userId);
    }

    @Override
    public Account getAccountById(int id) throws Exception {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Double getAccountBalance(int id) throws Exception {
        return accountRepository.findAccountBalance(id);
    }

    @Override
    public Account saveAccounts(Account account) throws Exception {
        return accountRepository.save(account);
    }
}
