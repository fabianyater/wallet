package com.wallet.service;

import com.wallet.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction saveTransaction(Transaction transaction) throws Exception;

    List<Transaction> getTransactions() throws Exception;

    List<Transaction> getTransactionsByAccountId(int accountId) throws Exception;

    Transaction getTransactionById(Integer id) throws Exception;

    Optional<Transaction> getTransactionSDetails(Integer txnId, Integer accountId) throws Exception;

    Double getTransactionAmountsByAccountId(int id) throws Exception;


}
