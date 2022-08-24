package com.wallet.controller;

import com.wallet.entity.Account;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.wallet.util.CommonMessages.ERROR_MESSAGE;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<GeneralResponse<Account>> save(@RequestBody Account account) {
        GeneralResponse<Account> response = new GeneralResponse<>();
        HttpStatus status;
        Account data = null;
        List<Account> accounts;
        String message;

        try {
            accounts = accountService.getAccounts();

            if (!containsName(accounts, account.getAccountName())) {
                data = accountService.saveAccount(account);
                message = "Account correctly created";
            } else {
                message = "Account name is already in use";
            }

            response.setMessage(message);
            response.setData(data);
            response.setCode(201);
            status = HttpStatus.CREATED;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping()
    public ResponseEntity<GeneralResponse<List<Account>>> getAccounts() {
        GeneralResponse<List<Account>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Account> accounts;
        String message;

        try {
            accounts = accountService.getAccounts();

            if (!accountService.getAccounts().isEmpty()) {
                message = "Found " + accounts.size() + " account(s)";
            } else {
                message = "No accounts found";
            }

            response.setMessage(message);
            response.setData(accounts);
            response.setCode(200);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
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

                accountService.saveAccount(data);
                message = "Account successfully created";
            }

            response.setMessage(message);
            response.setData(data);
            status = HttpStatus.CREATED;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<List<Account>>> getAccountsByUserId(@PathVariable("userId") Integer userId) {
        GeneralResponse<List<Account>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Account> userAccountList = null;
        String message;

        try {
            if (accountService.getAccountsByUserId(userId) == null) {
                message = "Not found";
            } else {
                userAccountList = accountService.getAccountsByUserId(userId);
                message = "User successfully found";
            }

            response.setMessage(message);
            response.setData(userAccountList);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{accountId}/user/{userId}")
    public ResponseEntity<GeneralResponse<Account>> getUserAccountsById(
            @PathVariable("accountId") Integer accountId,
            @PathVariable("userId") Integer userId) {

        GeneralResponse<Account> response = new GeneralResponse<>();
        HttpStatus status;
        Account userAccountById = null;
        String message;

        try {
            if (accountService.getAccountDetails(accountId, userId) == null) {
                message = "Not found";
            } else {
                userAccountById = accountService.getAccountDetails(accountId, userId);
                message = "User successfully found";
            }

            response.setMessage(message);
            response.setData(userAccountById);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
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
            response.setData(balance);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }
        return new ResponseEntity<>(response, status);
    }

    private boolean containsName(final List<Account> accounts, final String accountName) {
        return accounts.stream().anyMatch(o -> o.getAccountName().equals(accountName));
    }
}
