package com.wallet.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Integer transactionId;

    @Column(name = "amount", nullable = false)
    private Double transactionAmount;

    @Column(name = "description")
    private String transactionDescription;

    @Column(name = "type", nullable = false)
    private String transactionType;

    @Column(name = "category", nullable = false)
    private String transactionCategory;

    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date transactionDate;

    @Column(name = "time", nullable = false)
    private LocalTime transactionTime;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "account_id")
    private Account account;

}
