package com.aldren.messaging.service.impl;

import com.aldren.messaging.document.Users;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.repository.UsersRepository;
import com.aldren.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UsersRepository userRepo;

    @Override
    public void send(String sender, Message message) throws UserDoesNotExistException {
        List<String> userStr = new ArrayList<>();
        userStr.add(sender);
        userStr.add(message.getReceiver());

        List<Users> users = userRepo.findUsersByINAggregation(userStr);

        if(users.size() < 2) {
            for(Users user : users) {
                String userId = user.getUserId();
                if(!userStr.contains(userId)) {
                    throw new UserDoesNotExistException(String.format("User %s does not exists in the database.", userId));
                }
            }
        }

//        Users poster = userRepo.findByUserId(sender);
//
//        Optional<Users> recipient = Optional.ofNullable(userRepo.findByUserId(message.getReceiver()));
//
//        if(!recipient.isPresent()) {
//            throw new UserDoesNotExistException(String.format("User %s doesn't exists in the database", message.getReceiver()));
//        }
    }
}
