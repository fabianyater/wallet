package com.wallet.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(length = 50, nullable = false)
    private String fullname;

    @Column
    private String jwt;

    @OneToMany(mappedBy = "user")
    List<Account> accounts;
}
