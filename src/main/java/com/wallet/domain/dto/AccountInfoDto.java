package com.wallet.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountInfoDto implements Serializable {
    private Integer accountId;
    private String accountName;
    private String accountCurrency;
    private Double accountBalance;
}
