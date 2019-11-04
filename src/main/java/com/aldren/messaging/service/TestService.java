package com.aldren.messaging.service;

import com.aldren.messaging.constants.EnumConstants;
import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.repository.MessagesRepository;
import com.aldren.messaging.repository.UsersRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class TestService {

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private MessagesRepository messages;

    private Random randomGenerator = new Random();

    public void insertTestData() throws ParseException {
        String[] users = {"tonystark","steverogers","nickfury","mariahill","thorodinson"};

        for (int i = 0; i < 30; i++) {
            int daysAgo = (30 - i);

            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            String date = DateFormatUtils.format(new Date(System.currentTimeMillis() - (daysAgo * DAY_IN_MS)), HelperConstants.TIMESTAMP_FORMAT);

            List<Messages> messages = new ArrayList<>();

            int j = 0;
            while (j < (i + 1)) {
                int senderNum = randomGenerator.nextInt(5);
                Users sender = usersRepo.findByUserId(users[senderNum]);

                int receiverNum = randomGenerator.nextInt(5);
                while(receiverNum == senderNum) {
                    receiverNum = randomGenerator.nextInt(5);
                }
                Users receiver = usersRepo.findByUserId(users[receiverNum]);

                Messages message = new Messages();
                message.setSender(sender.getId());
                message.setReceiver(receiver.getId());
                message.setSubject("Test " + j);
                message.setContent(String.format("Test message %d from %d day/s ago", j, daysAgo));
                message.setSentDate(DateUtils.parseDate(date, HelperConstants.TIMESTAMP_FORMAT));
                message.setStatus(EnumConstants.MessageStatus.READ.toString());

                messages.add(message);

                j++;
            }

            this.messages.insert(messages);
        }
    }


}
