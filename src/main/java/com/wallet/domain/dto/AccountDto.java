package com.wallet.domain.dto;

import com.wallet.domain.entity.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDto implements Serializable {
    private Integer accountId;
    private String accountName;
    private String accountCurrency;
    private Double accountBalance;
    private User user;
}
