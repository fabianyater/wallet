package com.wallet.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GeneralResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T data;
    private boolean success;
    private String message;
    private int errorCode;
    private String messageResult;
    private String token;
}
