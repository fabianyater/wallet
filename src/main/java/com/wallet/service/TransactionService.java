package com.wallet.service;

import com.wallet.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction saveTransaction(Transaction transaction) throws Exception;

    List<Transaction> getTransactions() throws Exception;

    Transaction getTransactionById(Integer id) throws Exception;

}
