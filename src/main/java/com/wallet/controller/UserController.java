package com.wallet.controller;

import com.wallet.domain.dto.AccountInfoDto;
import com.wallet.domain.dto.UserDto;
import com.wallet.domain.dto.UserInfoDto;
import com.wallet.domain.entity.Account;
import com.wallet.domain.entity.User;
import com.wallet.model.GeneralResponse;
import com.wallet.service.AccountService;
import com.wallet.service.UserService;
import com.wallet.util.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    ModelMapper modelMapper;
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
    public ResponseEntity<GeneralResponse<UserDto>> createUser(@RequestBody UserDto userRequest) {
        GeneralResponse<UserDto> response = new GeneralResponse<>();
        HttpStatus status;
        User user = modelMapper.map(userRequest, User.class);
        User data;
        UserDto userDto = null;
        String message;
        List<User> usersList;

        try {
            usersList = userService.get();

            if (!containsName(usersList, user.getUsername())) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                data = userService.save(user);
                userDto = modelMapper.map(data, UserDto.class);
                message = "User successfully created";
            } else {
                message = "Username is already in use";
            }

            response.setMessage(message);
            response.setData(userDto);
            status = HttpStatus.CREATED;

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setMessage(ERROR_MESSAGE + e.getLocalizedMessage());
            response.setCode(500);
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<UserDto>> login(@RequestBody UserDto userRequest) {
        GeneralResponse<UserDto> response = new GeneralResponse<>();
        HttpStatus status;
        User user;
        UserDto userDto = null;
        String message;
        try {
            user = modelMapper.map(userRequest, User.class);
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            user.setJwt(jwtUtils.generateToken(user.getUsername()));
            user.setPassword(null);
            userDto = modelMapper.map(user, UserDto.class);
            message = "User: " + userDto.getUsername() + " successfully logged.";

            response.setMessage(message);
            response.setData(userDto);
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
    public ResponseEntity<GeneralResponse<Optional<UserInfoDto>>> getUserById(@PathVariable("id") Integer id) {
        GeneralResponse<Optional<UserInfoDto>> response = new GeneralResponse<>();
        HttpStatus status;
        Optional<User> user;
        Optional<UserInfoDto> userInfoDto = Optional.of(new UserInfoDto());
        String message;

        try {
            if (!userService.getById(id).isPresent()) {
                message = "User not found";
            } else {
                user = userService.getById(id);
                userInfoDto = user.map(this::convertToDto);
                message = "User successfully found";
            }

            response.setMessage(message);
            response.setData(userInfoDto);
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

    private UserInfoDto convertToDto(User user) {
        UserInfoDto userInfoDto = modelMapper.map(user, UserInfoDto.class);
        return userInfoDto;
    }
}
