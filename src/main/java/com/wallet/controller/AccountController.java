package com.wallet.controller;

import com.wallet.entity.Account;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.wallet.util.CommonMessages.ERROR_MESSAGE;
import static com.wallet.util.CommonMessages.SUCCESS_MESSAGE;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping()
    public ResponseEntity<GeneralResponse<Account>> save(@RequestBody Account account) {
        GeneralResponse<Account> response = new GeneralResponse<>();
        HttpStatus status;
        Account data;
        String message;

        try {
            data = accountService.saveAccounts(account);
            message = "Account correctly created";

            response.setMessage(message);
            response.setSuccess(true);
            response.setData(data);
            status = HttpStatus.OK;

        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping()
    public ResponseEntity<GeneralResponse<List<Account>>> getAccounts() {
        GeneralResponse<List<Account>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Account> account;
        String message;

        try {
            account = accountService.getAccounts();

            if (!account.isEmpty()) {
                message = "Found " + account.size() + " account(s)";
            } else {
                message = "There is no any account";
            }

            response.setMessage(message);
            response.setMessageResult(SUCCESS_MESSAGE);
            response.setSuccess(true);
            response.setData(account);
            status = HttpStatus.OK;

        } catch (Exception e) {
            String msg = ERROR_MESSAGE;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<GeneralResponse<Account>> updateAccount(@RequestBody Account account, @PathVariable("id") Integer id) {
        GeneralResponse<Account> response = new GeneralResponse<>();
        HttpStatus status;
        Account data = null;
        String message;

        try {
            if (accountService.getAccountById(id) == null) {
                message = "Account no found";
            } else {
                data = accountService.getAccountById(id);

                data.setAccountName(account.getAccountName());
                data.setAccountCurrency(account.getAccountCurrency());
                data.setAccountBalance(account.getAccountBalance());

                accountService.saveAccounts(data);
                message = "Account correctly created";

            }
            response.setMessageResult(SUCCESS_MESSAGE);
            response.setMessage(message);
            response.setSuccess(true);
            response.setData(data);
            status = HttpStatus.CREATED;

        } catch (Exception e) {
            String msg = ERROR_MESSAGE + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<GeneralResponse<Double>> getAccountBalance(@PathVariable("id") Integer id) {
        GeneralResponse<Double> response = new GeneralResponse<>();
        HttpStatus status;
        Double balance;
        String message;

        try {
            balance = accountService.getAccountBalance(id);
            message = "Balance value: " + balance;

            response.setMessage(message);
            response.setMessageResult(SUCCESS_MESSAGE);
            response.setSuccess(true);
            response.setData(balance);
            status = HttpStatus.OK;

        } catch (Exception e) {
            String msg = ERROR_MESSAGE;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }
}
