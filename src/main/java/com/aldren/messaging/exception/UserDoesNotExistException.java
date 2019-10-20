package com.aldren.messaging.exception;

import lombok.Getter;

@Getter
public class UserDoesNotExistException extends Exception {

    private String message;

    public UserDoesNotExistException(String message) {
        this.message = message;
    }

}
