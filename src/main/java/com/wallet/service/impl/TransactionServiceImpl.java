package com.wallet.service.impl;

import com.wallet.entity.Transaction;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction saveTransaction(Transaction transaction) throws Exception {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions() throws Exception {
        return transactionRepository.findAll();
    }
}
