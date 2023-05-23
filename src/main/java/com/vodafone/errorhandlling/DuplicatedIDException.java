package com.vodafone.errorhandlling;

import org.springframework.http.HttpStatus;

public class DuplicatedIDException extends DataException {
    public DuplicatedIDException(String message) {
        super(message);
    }
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}