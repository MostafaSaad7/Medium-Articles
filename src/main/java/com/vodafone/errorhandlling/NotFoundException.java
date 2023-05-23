package com.vodafone.errorhandlling;

import org.springframework.http.HttpStatus;

public class NotFoundException extends APIException{
    public NotFoundException(String message) {
        super(message);
    }
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
