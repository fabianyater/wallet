package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable = false)
    private Double transactionAmount;

    @Column(nullable = true)
    private String transactionDescription;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private String transactionCategory;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date transactionDate;

    @Column(nullable = false)
    private LocalTime transactionTime;

    @JsonIgnoreProperties(value = {"accountName", "accountCurrency", "accountBalance", "user"})
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private Account accountId;


}
