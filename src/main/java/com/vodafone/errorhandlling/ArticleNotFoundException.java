package com.vodafone.errorhandlling;

import com.vodafone.errorhandlling.DataException;
import org.springframework.http.HttpStatus;

public class ArticleNotFoundException extends DataException {
    public ArticleNotFoundException(String message) {
        super(message);
    }
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

