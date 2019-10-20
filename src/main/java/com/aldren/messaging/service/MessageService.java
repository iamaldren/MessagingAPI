package com.aldren.messaging.service;

import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;

public interface MessageService {

    void send(String sender, Message message) throws UserDoesNotExistException;

}
