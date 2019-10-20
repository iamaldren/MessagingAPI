package com.aldren.messaging.repository;

import com.aldren.messaging.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends MongoRepository<Users, Long> {
}
