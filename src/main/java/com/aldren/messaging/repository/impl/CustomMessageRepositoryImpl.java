package com.aldren.messaging.repository.impl;

import com.aldren.messaging.document.Messages;
import com.aldren.messaging.repository.CustomMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public int messageCountByDateDuration(Date startDate, Date endDate) {
        Query query = new Query(new Criteria().where("sentDate").gte(startDate).lt(endDate));

        return mongoTemplate.find(query, Messages.class).size();
    }
}
