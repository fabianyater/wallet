package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String accountCurrency;

    private Double accountAmount;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

}
