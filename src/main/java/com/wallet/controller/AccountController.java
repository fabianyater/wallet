package com.wallet.controller;

import com.wallet.domain.dto.AccountDto;
import com.wallet.domain.dto.UpdateAccountDto;
import com.wallet.domain.entity.Account;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;
import org.modelmapper.ModelMapper;
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
    ModelMapper modelMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<GeneralResponse<AccountDto>> save(@RequestBody AccountDto accountDtoRequest) {
        GeneralResponse<AccountDto> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        AccountDto accountDto = null;
        List<Account> accounts;
        String message;

        try {
            account = modelMapper.map(accountDtoRequest, Account.class);
            accounts = accountService.getAccountsByUserId(account.getUser().getUserId());

            if (!containsName(accounts, accountDtoRequest.getAccountName())) {
                accountService.saveAccount(account);
                accountDto = modelMapper.map(account, AccountDto.class);
                message = "Account correctly created";
            } else {
                message = "Account name is already in use";
            }

            response.setMessage(message);
            response.setData(accountDto);
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

    @PutMapping("/{accountId}/edit")
    public ResponseEntity<GeneralResponse<UpdateAccountDto>> updateAccount(@RequestBody UpdateAccountDto accountDtoRequest, @PathVariable("accountId") Integer accountId) {
        GeneralResponse<UpdateAccountDto> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        Account newAccount;
        List<Account> accounts;
        UpdateAccountDto updateAccountDto = null;
        String message;

        try {
            if (accountService.getAccountById(accountId) == null || accountService.getAccountById(accountId).getAccountId() != accountId) {
                message = "Account no found";
            } else {
                account = accountService.getAccountById(accountId);
                accounts = accountService.getAccountsByUserId(accountId);

                if (!containsName(accounts, accountDtoRequest.getAccountName())) {
                    account.setAccountName(accountDtoRequest.getAccountName());
                    account.setAccountCurrency(accountDtoRequest.getAccountCurrency());
                    account.setAccountBalance(accountDtoRequest.getAccountBalance());

                    newAccount = accountService.saveAccount(account);

                    updateAccountDto = modelMapper.map(newAccount, UpdateAccountDto.class);
                    message = "Account correctly updated";
                } else {
                    message = "Account name is already in use";
                }

            }

            response.setMessage(message);
            response.setData(updateAccountDto);
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
