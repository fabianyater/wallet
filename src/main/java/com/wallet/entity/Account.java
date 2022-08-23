package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

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

    private Double accountBalance;

    @ManyToOne
    @JsonIgnoreProperties(value = {"username", "password", "fullname", "jwt"})
    private User user;

}
