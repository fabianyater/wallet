package com.wallet.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GeneralResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T data;
    private String message;
    private int code;
}
