package com.wallet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalTime;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="transactionId")
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transactionDate;



    @Column(name = "time", nullable = false)
    private LocalTime transactionTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
