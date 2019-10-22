package com.aldren.messaging.exception;

import lombok.Getter;

@Getter
public class ReadMessageFailException extends Exception {

    private String message;

    public ReadMessageFailException(String message) {
        this.message = message;
    }

}
