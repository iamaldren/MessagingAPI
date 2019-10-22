package com.aldren.messaging.repository;

import com.aldren.messaging.document.Messages;

import java.util.List;

public interface CustomMessageRepository {

    int updateMessageStatus(List<Messages> messages);

}
