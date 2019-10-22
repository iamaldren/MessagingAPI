package com.aldren.messaging.repository.impl;

import com.aldren.messaging.constants.EnumConstants;
import com.aldren.messaging.document.Messages;
import com.aldren.messaging.repository.CustomMessageRepository;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    private static final String MESSAGES = "messages";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public int updateMessageStatus(List<Messages> messages) {
        List<WriteModel<Document>> update = messages.stream().map(message -> new UpdateOneModel<Document>(
                new Document("_id", new ObjectId(message.getId())), // filter
                new Document("$set", new Document("status", EnumConstants.MessageStatus.READ.toString())) // update
        )).collect(Collectors.toList());

        return mongoTemplate.getCollection(MESSAGES).bulkWrite(update).getModifiedCount();
    }
}
