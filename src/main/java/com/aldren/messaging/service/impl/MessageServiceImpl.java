package com.aldren.messaging.service.impl;

import com.aldren.messaging.constants.EnumConstants;
import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.exception.MessageDoesNotExistException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.MessageList;
import com.aldren.messaging.repository.MessagesRepository;
import com.aldren.messaging.repository.UsersRepository;
import com.aldren.messaging.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UsersRepository userRepo;
    @Autowired
    private MessagesRepository msgRepo;
    private ModelMapper mapper = new ModelMapper();

    @Override
    public void send(Message message) throws UserDoesNotExistException, ParseException {
        Users poster = userRepo.findByUserId(message.getSender());

        String date = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message.setSentDate(DateUtils.parseDate(date, HelperConstants.TIMESTAMP_FORMAT));

        Optional<Users> recipient = Optional.ofNullable(userRepo.findByUserId(message.getReceiver()));

        if (!recipient.isPresent()) {
            throw new UserDoesNotExistException(String.format("User %s doesn't exists in the database", message.getReceiver()));
        }

        Messages messages = mapMessage(message);
        messages.setSender(poster.getId());
        messages.setReceiver(recipient.get().getId());

        msgRepo.save(messages);
    }

    @Override
    public Message read(String messageId) throws MessageDoesNotExistException {
        Optional<Messages> messages = Optional.ofNullable(msgRepo.findByPrimaryId(messageId));

        if(!messages.isPresent()) {
            throw new MessageDoesNotExistException(String.format("Message with ID %s doesn't exists in the database", messageId));
        }

        return convertMessage(messages.get());
    }

    @Override
    public MessageList listMessages(String user, int page, String role) {
        Users users = userRepo.findByUserId(user);

        Pageable pageable = PageRequest.of(page, HelperConstants.PAGE_ITEMS_COUNT, getSort());

        Page<Messages> messages = null;
        if (HelperConstants.SENDER.equals(role)) {
            messages = listSentMessages(users.getId(), pageable);
        }

        if (HelperConstants.RECEIVER.equals(role)) {
            messages = listReceivedMessages(users.getId(), pageable);
        }

        if (messages == null) {
            throw new NullPointerException("Oops! Something went wrong. Please contact tech support.");
        }

        MessageList messageList = new MessageList();
        messageList.setTotalPage(messages.getTotalPages());
        messageList.setMessages(messages.getContent().stream().map(this::convertMessage).collect(Collectors.toList()));

        return messageList;
    }

    @Override
    public String messageCountPrediction(String forecast) {
        switch (forecast.toLowerCase()) {
            case HelperConstants.DAY:
                return predictMessageCountForTheDay();
            default:
                return predictMessageCountForTheWeek();
        }
    }

    private Sort getSort() {
        return new Sort(Sort.Direction.DESC, "sentDate");
    }

    private Page<Messages> listReceivedMessages(String userId, Pageable pageable) {
        return msgRepo.findAllReceivedMessages(userId, pageable);
    }

    private Page<Messages> listSentMessages(String userId, Pageable pageable) {
        return msgRepo.findAllSentMessages(userId, pageable);
    }

    private String predictMessageCountForTheDay() {
        int count = msgRepo.messageCountByDateDuration(new Date(System.currentTimeMillis() - (14 * HelperConstants.DAY_IN_MS)), new Date());
        return String.format("Predicted message count to receive for the day is %d", Math.round(count / 14));
    }

    private String predictMessageCountForTheWeek() {
        int count = msgRepo.messageCountByDateDuration(new Date(System.currentTimeMillis() - (30 * HelperConstants.DAY_IN_MS)), new Date());
        return String.format("Predicted message count to receive for the week is %d", Math.round(count / 4));
    }

    private Message convertMessage(Messages messages) {
        if(messages.getSender() != null) {
            Users sender = userRepo.findByPrimaryId(messages.getSender()).get(0);
            messages.setSender(String.format("%s %s", sender.getFirstName(), sender.getLastName()));
        }

        if(messages.getReceiver() != null) {
            Users receiver = userRepo.findByPrimaryId(messages.getReceiver()).get(0);
            messages.setReceiver(String.format("%s %s", receiver.getFirstName(), receiver.getLastName()));
        }

        return mapMessage(messages);
    }

    private Messages mapMessage(Message message) {
        return mapper.map(message, Messages.class);
    }

    private Message mapMessage(Messages messages) {
        return mapper.map(messages, Message.class);
    }

}
