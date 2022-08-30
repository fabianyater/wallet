package com.wallet.controller;

import com.wallet.domain.dto.AccountInfoDto;
import com.wallet.domain.dto.TransactionDto;
import com.wallet.domain.dto.TransactionInfoDto;
import com.wallet.domain.entity.Account;
import com.wallet.domain.entity.Transaction;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wallet.util.CommonMessages.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    private static final String INCOME = "INCOME";
    private static final String EXPENSE = "EXPENSE";

    @PostMapping()
    public ResponseEntity<GeneralResponse<TransactionDto>> createTransaction(@RequestBody TransactionDto transactionRequest) {
        GeneralResponse<TransactionDto> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        Transaction transaction;
        TransactionDto transactionDto = null;
        String message = "";

        try {
            transaction = modelMapper.map(transactionRequest, Transaction.class);

            if (transaction.getTransactionCategory() == null) {
                message = "Category not selected";
            } else {
                account = accountService.getAccountById(transaction.getAccount().getAccountId());
                setAccountBalance(account, transaction);
                transactionService.saveTransaction(transaction);
                message = "Transaction successfully created";
                transactionDto = modelMapper.map(transaction, TransactionDto.class);
            }

            response.setMessage(message);
            response.setData(transactionDto);
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/gmf")
    public ResponseEntity<GeneralResponse<TransactionDto>> createTAXOnFinancialMovements(@RequestBody TransactionDto transactionRequest) {
        GeneralResponse<TransactionDto> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        Transaction transaction;
        TransactionDto transactionDto = null;
        String message = "";
        Double totalAmount;
        Double gmf;
        Double totalBalance;
        int accountId = transactionRequest.getAccount().getAccountId();

        try {

            transaction = modelMapper.map(transactionRequest, Transaction.class);

            transaction.setTransactionCategory("Tax");
            transaction.setTransactionDescription("IMPUESTO GOBIERNO 4 X 1000");
            transaction.setTransactionType(EXPENSE);

            if (accountService.getAccountById(accountId) == null) {
                message = ACCOUNT_NOT_FOUND;
            } else {
                account = accountService.getAccountById(accountId);
                totalBalance = account.getAccountBalance();
                totalAmount = transactionService.getTransactionAmountsByAccountId(accountId);
                gmf = calculateGMF(totalAmount);
                totalBalance -= gmf;
                transaction.setTransactionAmount(gmf);
                account.setAccountBalance(totalBalance);
                transactionService.saveTransaction(transaction);

                transactionDto = modelMapper.map(transaction, TransactionDto.class);
                message = "Transaction successfully created";
            }

            response.setMessage(message);
            response.setData(transactionDto);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<GeneralResponse<List<TransactionInfoDto>>> getTransactionsByAccountId(@PathVariable("accountId") Integer accountId) {
        GeneralResponse<List<TransactionInfoDto>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Transaction> accountTransactions;
        List<TransactionInfoDto> transactionInfoDtos = null;
        String message;

        try {
            if (transactionService.getTransactionsByAccountId(accountId) == null) {
                message = "Not found";
            } else {
                accountTransactions = transactionService.getTransactionsByAccountId(accountId);
                transactionInfoDtos = accountTransactions.stream().map(this::convertToDto).collect(Collectors.toList());
                message = "Transactions successfully found. " + transactionInfoDtos.size() + " transactions";
            }

            response.setMessage(message);
            response.setData(transactionInfoDtos);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{txnId}/account/{accountId}")
    public ResponseEntity<GeneralResponse<Optional<TransactionInfoDto>>> getTransactionDetails(
            @PathVariable("txnId") Integer txnId,
            @PathVariable("accountId") Integer accountId) {
        GeneralResponse<Optional<TransactionInfoDto>> response = new GeneralResponse<>();
        HttpStatus status;
        Optional<Transaction> accountTransactions;
        Optional<TransactionInfoDto> transactionInfoDto = null;
        String message;

        try {

            if (!transactionService.getTransactionSDetails(txnId, accountId).isPresent()) {
                message = "Not found";
            } else {
                accountTransactions = transactionService.getTransactionSDetails(txnId, accountId);
                transactionInfoDto = accountTransactions.map(this::convertToDto);
                message = "Transaction successfully found";
            }

            response.setMessage(message);
            response.setData(transactionInfoDto);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    private Double calculateGMF(Double amount) {
        return (amount * 4) / 1000;
    }

    private void setAccountBalance(Account account, Transaction transaction) {
        Double totalBalance = account.getAccountBalance();
        Double transactionAmount = transaction.getTransactionAmount();
        String type = transaction.getTransactionType();

        if (type.equalsIgnoreCase(INCOME)) {
            totalBalance += transactionAmount;
            account.setAccountBalance(totalBalance);
        }
        if (type.equalsIgnoreCase(EXPENSE)) {
            totalBalance -= transactionAmount;
            account.setAccountBalance(totalBalance);
        }

    }

    private TransactionInfoDto convertToDto(Transaction transaction) {
        TransactionInfoDto transactionInfoDto = modelMapper.map(transaction, TransactionInfoDto.class);
        return transactionInfoDto;
    }

}
