package com.wallet.service;

import com.wallet.domain.entity.Account;

import java.util.List;

public interface AccountService {

    public Account saveAccount(Account account) throws Exception;

    public List<Account> getAccounts() throws Exception;

    public List<Account> getAccountsByUserId(int userId) throws Exception;

    public Account getAccountDetails(int accountId, int userId) throws Exception;


    Account getAccountById(int id) throws Exception;

    Double getAccountBalance(int id) throws Exception;

    Account getAccountByTxnId(int id) throws Exception;

}
