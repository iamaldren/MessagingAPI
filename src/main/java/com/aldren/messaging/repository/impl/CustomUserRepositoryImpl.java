package com.aldren.messaging.repository.impl;

import com.aldren.messaging.document.Users;
import com.aldren.messaging.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<Users> findUsersByINAggregation(List<String> users) {
        Query query = new Query(Criteria.where("userId").in(users));
        return mongoTemplate.find(query, Users.class);
    }
}
