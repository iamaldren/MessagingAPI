package com.aldren.messaging.service;

import com.aldren.messaging.exception.ReadMessageFailException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;

import java.text.ParseException;
import java.util.List;

public interface MessageService {

    void send(String sender, Message message) throws UserDoesNotExistException, ParseException;

    List<Message> read(String receiver) throws ReadMessageFailException;

}
