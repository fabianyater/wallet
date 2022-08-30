package com.wallet.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

@Data
public class TransactionInfoDto implements Serializable {
    private Integer transactionId;
    private Double transactionAmount;
    private String transactionDescription;
    private String transactionType;
    private String transactionCategory;
    private Date transactionDate;
    private LocalTime transactionTime;
}
