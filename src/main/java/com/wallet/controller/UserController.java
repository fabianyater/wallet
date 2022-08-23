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

    private static final String ERROR_MESSAGE = "Something has failed. Please contact support";

    @PostMapping("/signup")
    public ResponseEntity<GeneralResponse<User>> createUser(@RequestBody User user) {
        GeneralResponse<User> response = new GeneralResponse<>();
        HttpStatus status;
        User data = null;
        String message;
        List<User> usersList;

        try {
            usersList = userService.get();

            if (!containsName(usersList, user.getUsername())) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                data = userService.save(user);
                message = "User successfully created";
            } else {
                message = "Username is already in use";
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

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<User>> login(@RequestBody User user) {
        GeneralResponse<User> response = new GeneralResponse<>();
        HttpStatus status;
        String message;
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            user.setJwt(jwtUtils.generateToken(user.getUsername()));
            user.setPassword(null);
            message = "User: " + user.getUsername() + " successfully logged.";
            response.setMessage(message);
            response.setData(user);
            status = HttpStatus.CREATED;

        } catch (AuthenticationException authException) {
            message = "Incorrect user or password.";
            status = HttpStatus.FORBIDDEN;
            response.setMessage(message);
            response.setCode(status.value());
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<User>> getUserById(@PathVariable("id") Integer id) {
        GeneralResponse<User> response = new GeneralResponse<>();
        HttpStatus status;
        Optional<User> user = Optional.of(new User());
        String message;

        try {
            if (!userService.getById(id).isPresent()) {
                message = "User not found";
            } else {
                user = userService.getById(id);
                message = "User successfully found";
            }

            response.setMessage(message);
            response.setData(user.get());
            status = HttpStatus.OK;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    private boolean containsName(final List<User> users, final String username) {
        return users.stream().anyMatch(o -> o.getUsername().equals(username));
    }
}
