package com.wallet.service.impl;

import com.wallet.entity.Transaction;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) throws Exception {
        return transactionRepository.findByAccountId_AccountId(accountId);
    }

    @Override
    public Transaction getTransactionById(Integer id) throws Exception {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Transaction> getTransactionSDetails(Integer txnId, Integer accountId) throws Exception {
        return transactionRepository.findByTransactionIdAndAccountId_AccountId(txnId, accountId);
    }

}
