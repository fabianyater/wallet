package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.LinkedHashSet;
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

    private Double accountBalance;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id_user_id")
    private User userId;

    @OneToMany(mappedBy = "accountId", orphanRemoval = true)
    private Set<Transaction> transactions = new LinkedHashSet<>();

}
