package com.wallet.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private Integer userId;
    private String username;
    private String password;
    private String fullname;
    private String jwt;
}
