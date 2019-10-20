package com.aldren.messaging.service.impl;

import com.aldren.messaging.document.Users;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.repository.UsersRepository;
import com.aldren.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UsersRepository userRepo;

    @Override
    public void send(String sender, Message message) throws UserDoesNotExistException {
        Users poster = userRepo.findByUserId(sender);

        Optional<Users> recipient = Optional.ofNullable(userRepo.findByUserId(message.getReceiver()));

        if(!recipient.isPresent()) {
            throw new UserDoesNotExistException(String.format("User %s doesn't exists in the database", message.getReceiver()));
        }
    }
}
