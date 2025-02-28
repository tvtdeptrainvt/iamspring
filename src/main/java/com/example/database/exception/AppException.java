package com.example.database.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private ErrorCode errorcode;
    public AppException(ErrorCode errorcode) {
        super(errorcode.getMessage());
        this.errorcode = errorcode;
    }
}
