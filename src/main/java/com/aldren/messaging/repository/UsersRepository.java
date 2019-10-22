package com.aldren.messaging.repository;

import com.aldren.messaging.document.Users;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = {"users"})
public interface UsersRepository extends MongoRepository<Users, Long> {

    @Cacheable(key = "#userId")
    Users findByUserId(String userId);

    @Cacheable(key = "#id")
    @Query(value = "{'_id': ?0}", fields = "{userId: 1}")
    List<Users> findByPrimaryId(String id);

}
