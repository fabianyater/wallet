package com.wallet.controller;

import com.wallet.domain.dto.AccountDto;
import com.wallet.domain.dto.AccountInfoDto;
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
import java.util.stream.Collectors;

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

    @PutMapping("/{accountId}/edit")
    public ResponseEntity<GeneralResponse<AccountInfoDto>> updateAccount(@RequestBody AccountInfoDto accountDtoRequest, @PathVariable("accountId") Integer accountId) {
        GeneralResponse<AccountInfoDto> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        Account newAccount;
        List<Account> accounts;
        AccountInfoDto accountInfoDto = null;
        String message;

        try {
            if (accountService.getAccountById(accountId) == null || !accountService.getAccountById(accountId).getAccountId().equals(accountId)) {
                message = "Account no found";
            } else {
                account = accountService.getAccountById(accountId);
                accounts = accountService.getAccountsByUserId(accountId);

                if (!containsName(accounts, accountDtoRequest.getAccountName())) {
                    account.setAccountName(accountDtoRequest.getAccountName());
                    account.setAccountCurrency(accountDtoRequest.getAccountCurrency());
                    account.setAccountBalance(accountDtoRequest.getAccountBalance());

                    newAccount = accountService.saveAccount(account);

                    accountInfoDto = modelMapper.map(newAccount, AccountInfoDto.class);
                    message = "Account correctly updated";
                } else {
                    message = "Account name is already in use";
                }

            }

            response.setMessage(message);
            response.setData(accountInfoDto);
            status = HttpStatus.CREATED;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<List<AccountInfoDto>>> getAccountsByUserId(@PathVariable("userId") Integer userId) {
        GeneralResponse<List<AccountInfoDto>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Account> userAccountList = null;
        List<AccountInfoDto> accountDtos = null;
        String message;

        try {
            if (accountService.getAccountsByUserId(userId) == null) {
                message = "Not found";
            } else {
                userAccountList = accountService.getAccountsByUserId(userId);
                accountDtos = userAccountList.stream().map(this::convertToDto).collect(Collectors.toList());
                message = "User successfully found";
            }

            response.setMessage(message);
            response.setData(accountDtos);
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{accountId}/user/{userId}")
    public ResponseEntity<GeneralResponse<AccountInfoDto>> getUserAccountsById(
            @PathVariable("accountId") Integer accountId,
            @PathVariable("userId") Integer userId) {

        GeneralResponse<AccountInfoDto> response = new GeneralResponse<>();
        HttpStatus status;
        Account userAccountById;
        AccountInfoDto accountInfoDto = null;
        String message;

        try {
            if (accountService.getAccountDetails(accountId, userId) == null) {
                message = "Not found";
            } else {
                userAccountById = accountService.getAccountDetails(accountId, userId);
                accountInfoDto = convertToDto(userAccountById);
                message = "User successfully found";
            }

            response.setMessage(message);
            response.setData(accountInfoDto);
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

    private AccountInfoDto convertToDto(Account account) {
        AccountInfoDto accountInfoDto = modelMapper.map(account, AccountInfoDto.class);
        return accountInfoDto;
    }
}
