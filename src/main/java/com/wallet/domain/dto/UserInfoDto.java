package com.wallet.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDto implements Serializable {
    private Integer userId;
    private String username;
    private String fullname;
    private String jwt;
}
