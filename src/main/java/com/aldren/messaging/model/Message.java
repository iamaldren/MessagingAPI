package com.aldren.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    private String sender;

    private String receiver;

    private String subject;

    private String content;

    private String sentDate;

}
