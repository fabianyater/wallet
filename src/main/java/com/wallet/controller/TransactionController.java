package com.wallet.controller;

import com.wallet.domain.entity.Account;
import com.wallet.domain.entity.Transaction;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.wallet.util.CommonMessages.*;

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
        Account account;
        String message = "";

        try {
            if (transaction.getTransactionCategory() == null) {
                message = "Category not selected";
            } else {
                account = accountService.getAccountById(transaction.getAccount().getAccountId());
                Double totalBalance = account.getAccountBalance();
                totalBalance += transaction.getTransactionAmount();

                if (transaction.getTransactionType().equalsIgnoreCase(DEPOSIT)) {
                    account.setAccountBalance(totalBalance);
                    transactionService.saveTransaction(transaction);
                    message = "Transaction successfully created";
                }
            }

            response.setMessage(message);
            response.setData(transaction);
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<GeneralResponse<Transaction>> createWithdraw(@RequestBody Transaction transaction) {
        GeneralResponse<Transaction> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        String message = "";

        try {
            if (transaction.getTransactionCategory() == null) {
                message = "Category not selected";
            } else {
                account = accountService.getAccountById(transaction.getAccount().getAccountId());
                Double totalBalance = account.getAccountBalance();
                totalBalance -= transaction.getTransactionAmount();

                if (transaction.getTransactionType().equalsIgnoreCase(WITHDRAW)) {
                    account.setAccountBalance(totalBalance);
                    transactionService.saveTransaction(transaction);
                    message = "Transaction successfully created";
                }
            }

            response.setMessage(message);
            response.setData(transaction);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/gmf")
    public ResponseEntity<GeneralResponse<Transaction>> createTAXOnFinancialMovements(@RequestBody Transaction transaction) {
        GeneralResponse<Transaction> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        String message = "";
        Double totalAmount;
        Double gmf;
        Double totalBalance;
        int accountId = transaction.getAccount().getAccountId();

        try {
            transaction.setTransactionCategory("Tax");
            transaction.setTransactionDescription("IMPUESTO GOBIERNO 4 X 1000");
            transaction.setTransactionType(WITHDRAW);

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
                message = "Transaction successfully created";
            }

            response.setMessage(message);
            response.setData(transaction);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
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
                message = "Not found";
            } else {
                accountTransactions = transactionService.getTransactionsByAccountId(accountId);
                message = "Transactions successfully found. " + accountTransactions.size() + " transactions";
            }

            response.setMessage(message);
            response.setData(accountTransactions);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
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
                message = "Not found";
            } else {
                accountTransactions = transactionService.getTransactionSDetails(txnId, accountId);
                message = "Transaction successfully found";
            }

            response.setMessage(message);
            response.setData(accountTransactions);
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

}
