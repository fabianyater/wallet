package com.wallet.service;

import com.wallet.entity.Account;

import java.util.List;

public interface AccountService {

    public Account createAccount(Account account) throws Exception;

    public List<Account> getAccounts() throws Exception;

    Account getAccountById(int id) throws Exception;


}
