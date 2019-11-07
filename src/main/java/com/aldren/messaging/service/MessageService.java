package com.aldren.messaging.service;

import com.aldren.messaging.exception.MessageDoesNotExistException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.MessageList;

import java.text.ParseException;

public interface MessageService {

    void send(Message message) throws UserDoesNotExistException, ParseException;

    Message read(String messageId) throws MessageDoesNotExistException;

    MessageList listMessages(String user, int page, String role);

    String messageCountPrediction(String forecast);

}
