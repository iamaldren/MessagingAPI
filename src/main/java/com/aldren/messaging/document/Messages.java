package com.aldren.messaging.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "messages")
public class Messages {

    @Id
    private String id;

    private String sender;

    private String receiver;

    private String subject;

    private String content;

    private String sentDate;

    private String status;

}
