package com.aldren.messaging.repository;

import com.aldren.messaging.document.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessagesRepository extends MongoRepository<Messages, Long>, CustomMessageRepository {

    @Query(value = "{'receiver' : ?0, 'status' : 'UNREAD'}")
    List<Messages> findUnreadMessages(String user, Sort sort);

    @Query(value = "{'receiver' : ?0}")
    Page<Messages> findAllReceivedMessages(String user, Pageable pageable);

    @Query(value = "{'sender' : ?0}")
    Page<Messages> findAllSentMessages(String user, Pageable pageable);

}
