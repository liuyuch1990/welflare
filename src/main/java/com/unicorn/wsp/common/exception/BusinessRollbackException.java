package com.unicorn.wsp.common.exception;

public class BusinessRollbackException extends RuntimeException{
    public BusinessRollbackException(String message){
        super(message);
    }
}

