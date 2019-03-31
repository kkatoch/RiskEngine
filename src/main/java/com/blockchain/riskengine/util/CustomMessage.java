package com.blockchain.riskengine.util;

import org.springframework.http.HttpStatus;

public class CustomMessage {
    HttpStatus httpStatus;
    String message;

    public CustomMessage() {
    }

    public CustomMessage(String message, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
