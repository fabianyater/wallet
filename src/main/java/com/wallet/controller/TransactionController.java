package com.wallet.controller;

import com.wallet.entity.Account;
import com.wallet.entity.Transaction;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wallet.util.CommonMessages.ERROR_MESSAGE;
import static com.wallet.util.CommonMessages.SUCCESS_MESSAGE;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAW = "WITHDRAW";

    @PostMapping("/deposit")
    public ResponseEntity<GeneralResponse<Transaction>> createDeposit(@RequestBody Transaction transaction) {
        GeneralResponse<Transaction> response = new GeneralResponse<>();
        HttpStatus status;
        Account data;
        String message = "";

        try {
            if (transaction.getTransactionCategory() == null) {
                message = "Category not selected";
            } else {
                data = accountService.getAccountById(transaction.getAccountId().getAccountId());
                Double totalBalance = data.getAccountBalance();
                totalBalance += transaction.getTransactionAmount();

                if (transaction.getTransactionType().equalsIgnoreCase(DEPOSIT)) {
                    data.setAccountBalance(totalBalance);
                    transactionService.saveTransaction(transaction);
                }
            }

            response.setMessage(message);
            response.setSuccess(true);
            response.setData(transaction);
            status = HttpStatus.OK;
        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<GeneralResponse<Transaction>> createWithdraw(@RequestBody Transaction transaction) {
        GeneralResponse<Transaction> response = new GeneralResponse<>();
        HttpStatus status;
        Account data;
        String message = "";

        try {
            if (transaction.getTransactionCategory() == null) {
                message = "Category not selected";
            } else {
                data = accountService.getAccountById(transaction.getAccountId().getAccountId());
                Double totalBalance = data.getAccountBalance();
                totalBalance -= transaction.getTransactionAmount();

                if (transaction.getTransactionType().equalsIgnoreCase(WITHDRAW)) {
                    data.setAccountBalance(totalBalance);
                    transactionService.saveTransaction(transaction);
                }
            }

            response.setMessage(message);
            response.setSuccess(true);
            response.setData(transaction);
            status = HttpStatus.OK;
        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/gmf")
    public ResponseEntity<GeneralResponse<Transaction>> createTAXOnFinancialMovements(@RequestBody Transaction transaction) {
        GeneralResponse<Transaction> response = new GeneralResponse<>();
        HttpStatus status;
        Account data;
        String message = "";
        Double totalAmount;
        Double gmf;
        Double totalBalance;
        int accountId = transaction.getAccountId().getAccountId();

        try {
            transaction.setTransactionCategory("Tax");
            transaction.setTransactionDescription("IMPUESTO GOBIERNO 4 X 1000");
            transaction.setTransactionType(WITHDRAW);

            data = accountService.getAccountById(accountId);
            totalBalance = data.getAccountBalance();
            totalAmount = transactionService.getTransactionAmountsByAccountId(accountId);
            gmf = calculateGMF(totalAmount);
            totalBalance -= gmf;

            transaction.setTransactionAmount(gmf);
            data.setAccountBalance(totalBalance);
            transactionService.saveTransaction(transaction);

            response.setMessage(message);
            response.setSuccess(true);
            response.setData(transaction);
            status = HttpStatus.OK;
        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<GeneralResponse<List<Transaction>>> getTransactionsByAccountId(@PathVariable("accountId") Integer accountId) {
        GeneralResponse<List<Transaction>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Transaction> accountTransactions = null;
        String message;

        try {
            if (transactionService.getTransactionsByAccountId(accountId) == null) {
                response.setErrorCode(1);
                response.setMessageResult("Not found");
            } else {
                accountTransactions = transactionService.getTransactionsByAccountId(accountId);
                response.setErrorCode(0);
                response.setMessageResult("Transactions successfully found. " + accountTransactions.size() + " transactions");
            }

            message = SUCCESS_MESSAGE;
            response.setMessage(message);
            response.setSuccess(true);
            response.setData(accountTransactions);
            status = HttpStatus.OK;

        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{txnId}/account/{accountId}")
    public ResponseEntity<GeneralResponse<Optional<Transaction>>> getTransactionDetails(
            @PathVariable("txnId") Integer txnId,
            @PathVariable("accountId") Integer accountId) {
        GeneralResponse<Optional<Transaction>> response = new GeneralResponse<>();
        HttpStatus status;
        Optional<Transaction> accountTransactions = null;
        String message;

        try {

            if (!transactionService.getTransactionSDetails(txnId, accountId).isPresent()) {
                response.setErrorCode(1);
                response.setMessageResult("Not found");
            } else {
                accountTransactions = transactionService.getTransactionSDetails(txnId, accountId);
                response.setErrorCode(0);
                response.setMessageResult("Transactions successfully found. " + accountTransactions.get() + " transactions");
            }

            message = SUCCESS_MESSAGE;
            response.setMessage(message);
            response.setSuccess(true);
            response.setData(accountTransactions);
            status = HttpStatus.OK;

        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    private Double calculateGMF(Double amount) {
        return (amount * 4) / 1000;
    }

}
