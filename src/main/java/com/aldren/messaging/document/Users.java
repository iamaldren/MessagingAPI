package com.aldren.messaging.document;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "users")
public class Users {

    @Id
    private String id;

    private String userId;

    private String firstName;

    private String lastName;

    private String status;

    private String role;

    private String updatedDate;

}
