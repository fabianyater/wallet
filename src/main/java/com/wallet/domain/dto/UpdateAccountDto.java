package com.wallet.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateAccountDto implements Serializable {
    private String accountName;
    private String accountCurrency;
    private Double accountBalance;
}
