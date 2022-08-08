package com.wallet.controller;

import com.wallet.entity.Account;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping()
    public ResponseEntity<GeneralResponse<Account>> save(@RequestBody Account account) {

        GeneralResponse<Account> response = new GeneralResponse<>();
        HttpStatus status = null;
        Account data = null;
        String message = null;

        try {

            data = accountService.createAccount(account);
            message = "Account correctly created";

            response.setMessage(message);
            response.setSuccess(true);
            response.setData(data);
            status = HttpStatus.OK;

        } catch (Exception e) {

            String msg = "Something has failed. Please contact suuport." + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping()
    public ResponseEntity<GeneralResponse<List<Account>>> getAccounts() {

        GeneralResponse<List<Account>> response = new GeneralResponse<>();
        HttpStatus status = null;
        List<Account> account = null;
        String message = "";

        try {
            account = accountService.getAccounts();

            if (!account.isEmpty()) {
                message = "Found " + account.size() + " account(s)";
            } else {
                message = "There is no any account";
            }

            response.setMessage(message);
            response.setMessageResult("Succesful transaction");
            response.setSuccess(true);
            response.setData(account);
            status = HttpStatus.OK;

        } catch (Exception e) {

            String msg = "Something has failed. Please contact suuport.";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    private boolean containsName(final List<Account> accounts, final String accountName) {
        return accounts.stream().filter(o -> o.getAccountName().equals(accountName)).findFirst().isPresent();
    }
}
