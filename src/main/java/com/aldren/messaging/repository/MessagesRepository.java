package com.aldren.messaging.repository;

import com.aldren.messaging.document.Messages;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"messages"})
public interface MessagesRepository extends MongoRepository<Messages, Long>, CustomMessageRepository {

    @Cacheable(key = "#id")
    @Query(value = "{'_id': ?0}")
    Messages findByPrimaryId(String id);

    @Query(value = "{'receiver' : ?0}", fields = "{receiver: 0, content: 0}")
    Page<Messages> findAllReceivedMessages(String user, Pageable pageable);

    @Query(value = "{'sender' : ?0}", fields = "{sender: 0, content: 0}")
    Page<Messages> findAllSentMessages(String user, Pageable pageable);

}
