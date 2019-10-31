package com.aldren.messaging.service.impl;

import com.aldren.messaging.constants.EnumConstants;
import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.exception.ReadMessageFailException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
    @Autowired
    private UsersRepository userRepo;
    @Autowired
    private MessagesRepository msgRepo;
    private ModelMapper mapper = new ModelMapper();

    @Override
    public void send(String sender, Message message) throws UserDoesNotExistException, ParseException {
        Users poster = userRepo.findByUserId(sender);

        message.setSender(sender);

        String date = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message.setSentDate(DateUtils.parseDate(date, HelperConstants.TIMESTAMP_FORMAT));

        Optional<Users> recipient = Optional.ofNullable(userRepo.findByUserId(message.getReceiver()));

        if (!recipient.isPresent()) {
            throw new UserDoesNotExistException(String.format("User %s doesn't exists in the database", message.getReceiver()));
        }

        Messages messages = mapMessage(message);
        messages.setSender(poster.getId());
        messages.setReceiver(recipient.get().getId());
        messages.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        msgRepo.save(messages);
    }

    @Override
    public List<Message> read(String receiver) throws ReadMessageFailException {
        Users recipient = userRepo.findByUserId(receiver);
        List<Messages> messages = msgRepo.findUnreadMessages(recipient.getId(), getSort());

        if (!messages.isEmpty()) {
            int updateCount = msgRepo.updateMessageStatus(messages);
            if (updateCount < messages.size()) {
                throw new ReadMessageFailException("Something went wrong in reading the message.");
            }
        }

        return messages.stream().map(this::convertMessage).collect(Collectors.toList());
    }

    @Override
    public List<Message> listMessages(String user, int page, String role) {
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

        return messages.getContent().stream().map(this::convertMessage).collect(Collectors.toList());
    }

    @Override
    public String messageCountPrediction(String type) {
        switch (type.toLowerCase()) {
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
        int count = msgRepo.messageCountByDateDuration(new Date(System.currentTimeMillis() - (14 * DAY_IN_MS)), new Date());
        return String.format("Predicted message count to receive for the day is %d", (count / 14));
    }

    private String predictMessageCountForTheWeek() {
        int count = msgRepo.messageCountByDateDuration(new Date(System.currentTimeMillis() - (30 * DAY_IN_MS)), new Date());
        return String.format("Predicted message count to receive for the week is %d", (count / 4));
    }

    private Message convertMessage(Messages messages) {
        messages.setSender(userRepo.findByPrimaryId(messages.getSender()).get(0).getUserId());
        messages.setReceiver(userRepo.findByPrimaryId(messages.getReceiver()).get(0).getUserId());
        return mapMessage(messages);
    }

    private Messages mapMessage(Message message) {
        return mapper.map(message, Messages.class);
    }

    private Message mapMessage(Messages messages) {
        return mapper.map(messages, Message.class);
    }

}
