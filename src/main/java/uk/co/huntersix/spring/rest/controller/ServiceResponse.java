package uk.co.huntersix.spring.rest.controller;

import org.springframework.http.HttpStatus;

public class ServiceResponse {

    private String message;
    private HttpStatus httpStatus;
    private Object response;

    public ServiceResponse() {
    }

    public ServiceResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ServiceResponse(String message, HttpStatus httpStatus, Object response) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
