package com.aldren.messaging.repository;

import com.aldren.messaging.document.Users;

import java.util.List;

public interface CustomUserRepository {

    List<Users> findUsersByINAggregation(List<String> users);

}
