package com.vodafone.errorhandlling;

import org.springframework.http.HttpStatus;

public abstract class DataException extends RuntimeException{
    public DataException(String message) {
        super(message);
    }
    public abstract HttpStatus getStatus();
}
