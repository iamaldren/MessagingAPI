package com.aldren.messaging.unit.service;

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
import com.aldren.messaging.service.impl.MessageServiceImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MessageServiceImpl.class})
public class MessageServiceTest {

    private static final String SENDER = "tonystark";
    private static final String RECEIVER = "steverogers";
    private static final String SENDER_ID = "0000001";
    private static final String RECEIVER_ID = "0000001";
    @Autowired
    private MessageService svc;
    @MockBean
    private UsersRepository userRepo;
    @MockBean
    private MessagesRepository msgRepo;
    private Users sender = new Users();
    private Users receiver = new Users();
    private Message message = new Message();

    @Before
    public void setUp() {
        sender.setId(SENDER_ID);
        sender.setUserId(SENDER);
        sender.setFirstName("Tony");
        sender.setLastName("Stark");
        sender.setRole(EnumConstants.UserRole.USER.toString());
        sender.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        receiver.setId(RECEIVER_ID);
        receiver.setUserId(RECEIVER);
        receiver.setFirstName("Steve");
        receiver.setLastName("Rogers");
        receiver.setRole(EnumConstants.UserRole.USER.toString());
        receiver.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        message.setReceiver(RECEIVER);
        message.setSubject("Test");
        message.setContent("Message Content!");

        Mockito.when(userRepo.findByUserId(eq(SENDER))).thenReturn(sender);
        Mockito.when(userRepo.findByUserId(eq(RECEIVER))).thenReturn(receiver);

        List<Users> senders = new ArrayList<>();
        senders.add(sender);
        Mockito.when(userRepo.findByPrimaryId(eq(SENDER_ID))).thenReturn(senders);

        List<Users> receivers = new ArrayList<>();
        receivers.add(receiver);
        Mockito.when(userRepo.findByPrimaryId(eq(RECEIVER_ID))).thenReturn(receivers);
    }

    @Test
    public void testSendSuccess() throws UserDoesNotExistException, ParseException {
        svc.send(SENDER, message);

        Mockito.verify(msgRepo, Mockito.times(1)).save(Mockito.any());
    }

    @Test(expected = UserDoesNotExistException.class)
    public void testSendFailure() throws UserDoesNotExistException, ParseException {
        Mockito.when(userRepo.findByUserId(message.getReceiver())).thenReturn(null);

        svc.send(SENDER, message);

        Mockito.verify(msgRepo, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void testReadSuccessWithValues() throws ParseException, ReadMessageFailException {
        Messages messages1 = new Messages();
        messages1.setId("000000A");
        messages1.setSender(SENDER_ID);
        messages1.setReceiver(RECEIVER_ID);
        messages1.setSubject("Test 1");
        messages1.setContent("Message Content 1");
        messages1.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages2 = new Messages();
        messages2.setId("000000B");
        messages2.setSender(SENDER_ID);
        messages2.setReceiver(RECEIVER_ID);
        messages2.setSubject("Test 2");
        messages2.setContent("Message Content 2");
        messages2.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        List<Messages> messages = new ArrayList<>();
        messages.add(messages2);
        messages.add(messages1);

        Mockito.when(msgRepo.findUnreadMessages(Mockito.anyString(), Mockito.any())).thenReturn(messages);
        Mockito.when(msgRepo.updateMessageStatus(messages)).thenReturn(2);

        List<Message> message = svc.read(RECEIVER);

        assertThat(message.get(0).getReceiver()).isEqualTo(messages2.getReceiver());
        assertThat(message.get(0).getSender()).isEqualTo(messages2.getSender());
        assertThat(message.get(0).getSubject()).isEqualTo(messages2.getSubject());
        assertThat(message.get(0).getContent()).isEqualTo(messages2.getContent());

        assertThat(message.get(1).getReceiver()).isEqualTo(messages1.getReceiver());
        assertThat(message.get(1).getSender()).isEqualTo(messages1.getSender());
        assertThat(message.get(1).getSubject()).isEqualTo(messages1.getSubject());
        assertThat(message.get(1).getContent()).isEqualTo(messages1.getContent());
    }

    @Test
    public void testReadSuccessButEmpty() throws ParseException, ReadMessageFailException {
        List<Messages> messages = new ArrayList<>();

        Mockito.when(msgRepo.findUnreadMessages(Mockito.anyString(), Mockito.any())).thenReturn(messages);

        List<Message> message = svc.read(RECEIVER);

        assertThat(message.size()).isEqualTo(0);
    }

    @Test(expected = ReadMessageFailException.class)
    public void testReadSuccessWithException() throws ParseException, ReadMessageFailException {
        Messages messages1 = new Messages();
        messages1.setId("000000A");
        messages1.setSender(SENDER_ID);
        messages1.setReceiver(RECEIVER_ID);
        messages1.setSubject("Test 1");
        messages1.setContent("Message Content 1");
        messages1.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages2 = new Messages();
        messages2.setId("000000B");
        messages2.setSender(SENDER_ID);
        messages2.setReceiver(RECEIVER_ID);
        messages2.setSubject("Test 2");
        messages2.setContent("Message Content 2");
        messages2.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        List<Messages> messages = new ArrayList<>();
        messages.add(messages2);
        messages.add(messages1);

        Mockito.when(msgRepo.findUnreadMessages(Mockito.anyString(), Mockito.any())).thenReturn(messages);
        Mockito.when(msgRepo.updateMessageStatus(messages)).thenReturn(1);

        List<Message> message = svc.read(RECEIVER);
    }

}
