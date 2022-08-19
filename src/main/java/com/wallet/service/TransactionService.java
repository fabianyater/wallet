package com.wallet.service;

import com.wallet.entity.Transaction;

public interface TransactionService {

    Transaction saveTransaction(Transaction transaction) throws Exception;

}
