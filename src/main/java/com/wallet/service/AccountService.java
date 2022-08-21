package com.wallet.service;

import com.wallet.entity.Account;

import java.util.List;

public interface AccountService {

    public Account saveAccounts(Account account) throws Exception;

    public List<Account> getAccounts() throws Exception;

    Account getAccountById(int id) throws Exception;

    Double getAccountBalance(int id) throws Exception;

    Double getTransactionAmountsByAccountId(int id) throws Exception;


}
