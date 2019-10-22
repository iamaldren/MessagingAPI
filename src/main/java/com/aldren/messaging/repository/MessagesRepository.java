package com.aldren.messaging.repository;

import com.aldren.messaging.document.Messages;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessagesRepository extends MongoRepository<Messages, Long>, CustomMessageRepository {

    @Query(value = "{'status': 'UNREAD'}")
    List<Messages> findUnreadMessages();

}
