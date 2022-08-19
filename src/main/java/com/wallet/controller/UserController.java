package com.wallet.controller;

import com.wallet.entity.Account;
import com.wallet.entity.User;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.UserService;
import com.wallet.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<GeneralResponse<User>> save(@RequestBody User user) {

        GeneralResponse<User> response = new GeneralResponse<>();
        HttpStatus status = null;
        User data = null;
        String message = null;
        List<User> usersList = null;

        try {
            usersList = userService.get();

            if (!containsName(usersList, user.getUsername())) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                data = userService.save(user);
                message = "It Save " + data + " users.";
            } else {
                message = "Username is already in use";
            }

            response.setMessage(message);
            response.setSuccess(true);
            response.setData(data);
            status = HttpStatus.OK;

        } catch (Exception e) {

            String msg = "Something has failed. Please contact suuport.";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<User>> login(@RequestBody User user) {
        GeneralResponse<User> response = new GeneralResponse<>();
        HttpStatus status = null;
        String messageResult = "";
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            user.setJwt(jwtUtils.generateToken(user.getUsername()));
            user.setPassword(null);
            messageResult = "Login successfull for user: " + user.getUsername() + ".";

            response.setToken(user.getJwt());
            response.setMessageResult(messageResult);
            response.setMessage("Operation successfull");
            //TODO Create common class for messages
            response.setErrorCode(1);
            response.setSuccess(true);
            response.setData(user);
            status = HttpStatus.CREATED;

        } catch (AuthenticationException authException) {
            String message = "Incorrect user or password.";
            status = HttpStatus.FORBIDDEN;
            response.setMessage(message);
            response.setSuccess(false);
            response.setErrorCode(0);
        } catch (Exception e) {
            String message = "Something went wrong. Please contact support.";
            status = HttpStatus.FORBIDDEN;
            response.setMessage(message);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<User>> getUserById(@PathVariable("id") Integer id) {

        GeneralResponse<User> response = new GeneralResponse<>();
        HttpStatus status = null;
        Optional<User> user = null;
        String message = "";

        try {
            user = userService.getById(id);

            if (user == null || user.get().getUserId() == null) {
                response.setErrorCode(1);
                response.setMessageResult("Usert not found");
            } else {
                response.setErrorCode(0);
                response.setMessageResult("User with id: " + user.get().getUserId() + " succesfully found");
            }

            message = "Succesful transaction";
            response.setMessage(message);
            response.setSuccess(true);
            response.setData(user.get());
            status = HttpStatus.OK;

        } catch (Exception e) {

            String msg = "Something has failed. Please contact suuport.";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{userId}/accounts")
    public ResponseEntity<GeneralResponse<List<Account>>> getUserAccounts(@PathVariable("userId") Integer userId) {

        GeneralResponse<List<Account>> response = new GeneralResponse<>();
        HttpStatus status;
        List<Account> account;
        List<Account> userAccountList = new ArrayList<Account>();

        String message = "";

        try {
            //TODO: Create query in userRepository to get all user accounts instead of doing logic in Controller
            //account = userService.getUserAccounts(userId);
            account = accountService.getAccounts();

            if (!userService.getById(userId).isPresent()) {
                response.setErrorCode(1);
                response.setMessageResult("Not found");
            } else {
                for (int i = 0; i < account.size(); i++) {
                    if (account.get(i).getUser().getUserId().equals(userId)) {
                        userAccountList.add(account.get(i));
                    }
                }
                response.setErrorCode(0);
                response.setMessageResult("User succesfully found");
            }

            message = "Succesful transaction";
            response.setMessage(message);
            response.setSuccess(true);
            response.setData(userAccountList);
            status = HttpStatus.OK;

        } catch (Exception e) {

            String msg = "Something has failed. Please contact suuport." + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{userId}/accounts/{accountId}")
    public ResponseEntity<GeneralResponse<Account>> getUserAccountsById(
            @PathVariable("userId") Integer userId,
            @PathVariable("accountId") Integer accountId) {

        GeneralResponse<Account> response = new GeneralResponse<>();
        HttpStatus status;
        Account account;
        Account userAccountById = null;

        String message = "";

        try {
            if (!userService.getById(userId).isPresent() || accountService.getAccountById(accountId) == null) {
                response.setErrorCode(1);
                response.setMessageResult("Not found");
            } else {
                account = accountService.getAccountById(accountId);

                if (account.getUser().getUserId().equals(userId)) {
                    userAccountById = account;
                }

                response.setErrorCode(0);
                response.setMessageResult("User succesfully found");
            }
            message = "Succesful transaction";
            response.setMessage(message);
            response.setSuccess(true);
            response.setData(userAccountById);
            status = HttpStatus.OK;

        } catch (Exception e) {
            String msg = "Something has failed. Please contact suuport." + e.getLocalizedMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(msg);
            response.setSuccess(false);
        }

        return new ResponseEntity<>(response, status);
    }

    private boolean containsName(final List<User> users, final String username) {
        return users.stream().filter(o -> o.getUsername().equals(username)).findFirst().isPresent();
    }
}
