package com.vodafone.errorhandlling;

import com.vodafone.model.ErrorDetails;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorDetails> handleApiException(APIException apiException ){
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(apiException.getStatus().getReasonPhrase());
        errorDetails.setMessage(apiException.getMessage());
        return new ResponseEntity<>(errorDetails, apiException.getStatus());
    }
    @ExceptionHandler(DataException.class)
    public ResponseEntity<ErrorDetails> handleApiException(DataException dataException){
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(dataException.getStatus().getReasonPhrase());
        errorDetails.setMessage(dataException.getMessage());
        return new ResponseEntity<>(errorDetails, dataException.getStatus());
    }

    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorDetails.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
