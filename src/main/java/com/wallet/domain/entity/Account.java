package com.wallet.domain.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="accountId")
@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer accountId;

    @Column(name = "name", nullable = false)
    private String accountName;

    @Column(name = "currency", nullable = false)
    private String accountCurrency;

    @Column(name = "balance")
    private Double accountBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("accounts")
    private User user;

    @OneToMany(mappedBy = "account", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new LinkedHashSet<>();
}
