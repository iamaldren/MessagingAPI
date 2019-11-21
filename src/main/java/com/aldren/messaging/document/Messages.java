package com.aldren.messaging.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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

    private Date sentDate;

    private String status;

}
