package com.aldren.messaging.exception;

public class MessageDoesNotExistException extends Exception {

    private String message;

    public MessageDoesNotExistException(String message) {
        this.message = message;
    }

}
