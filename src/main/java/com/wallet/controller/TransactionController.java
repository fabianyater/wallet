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

import static com.wallet.util.CommonMessages.ERROR_MESSAGE;

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
        HttpStatus  status;
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
            totalAmount = accountService.getTransactionAmountsByAccountId(accountId);
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

    private Double calculateGMF(Double amount){
        return (amount * 4) / 1000;
    }

}
