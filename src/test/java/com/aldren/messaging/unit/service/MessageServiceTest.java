package com.aldren.messaging.unit.service;

import com.aldren.messaging.constants.EnumConstants;
import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.exception.ReadMessageFailException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.MessageList;
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
import org.springframework.data.domain.PageImpl;
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

    private static final String USER1 = "tonystark";
    private static final String USER2 = "steverogers";
    private static final String USER3 = "mariahill";
    private static final String USER4 = "nickfury";
    private static final String USER5 = "thorodinson";
    private static final String USER1_ID = "0000001";
    private static final String USER2_ID = "0000002";
    private static final String USER3_ID = "0000003";
    private static final String USER4_ID = "0000004";
    private static final String USER5_ID = "0000005";
    @Autowired
    private MessageService svc;
    @MockBean
    private UsersRepository userRepo;
    @MockBean
    private MessagesRepository msgRepo;
    private Users tonystark = new Users();
    private Users steverogers = new Users();
    private Users mariahill = new Users();
    private Users nickfury = new Users();
    private Users thorodinson = new Users();
    private Message message = new Message();

    @Before
    public void setUp() {
        tonystark.setId(USER1_ID);
        tonystark.setUserId(USER1);
        tonystark.setFirstName("Tony");
        tonystark.setLastName("Stark");
        tonystark.setRole(EnumConstants.UserRole.USER.toString());
        tonystark.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        steverogers.setId(USER2_ID);
        steverogers.setUserId(USER2);
        steverogers.setFirstName("Steve");
        steverogers.setLastName("Rogers");
        steverogers.setRole(EnumConstants.UserRole.USER.toString());
        steverogers.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        mariahill.setId(USER3_ID);
        mariahill.setUserId(USER3);
        mariahill.setFirstName("Maria");
        mariahill.setLastName("Hill");
        mariahill.setRole(EnumConstants.UserRole.USER.toString());
        mariahill.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        nickfury.setId(USER4_ID);
        nickfury.setUserId(USER4);
        nickfury.setFirstName("Nick");
        nickfury.setLastName("Fury");
        nickfury.setRole(EnumConstants.UserRole.USER.toString());
        nickfury.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        thorodinson.setId(USER5_ID);
        thorodinson.setUserId(USER5);
        thorodinson.setFirstName("Thor");
        thorodinson.setLastName("Odinson");
        thorodinson.setRole(EnumConstants.UserRole.USER.toString());
        thorodinson.setStatus(EnumConstants.UserStatus.ACTIVE.toString());

        message.setReceiver(USER2);
        message.setSubject("Test");
        message.setContent("Message Content!");

        Mockito.when(userRepo.findByUserId(eq(USER1))).thenReturn(tonystark);
        Mockito.when(userRepo.findByUserId(eq(USER2))).thenReturn(steverogers);
        Mockito.when(userRepo.findByUserId(eq(USER3))).thenReturn(mariahill);
        Mockito.when(userRepo.findByUserId(eq(USER4))).thenReturn(nickfury);
        Mockito.when(userRepo.findByUserId(eq(USER5))).thenReturn(thorodinson);

        List<Users> ts = new ArrayList<>();
        ts.add(tonystark);
        Mockito.when(userRepo.findByPrimaryId(eq(USER1_ID))).thenReturn(ts);

        List<Users> sr = new ArrayList<>();
        sr.add(steverogers);
        Mockito.when(userRepo.findByPrimaryId(eq(USER2_ID))).thenReturn(sr);

        List<Users> mh = new ArrayList<>();
        mh.add(mariahill);
        Mockito.when(userRepo.findByPrimaryId(eq(USER3_ID))).thenReturn(mh);

        List<Users> nf = new ArrayList<>();
        nf.add(nickfury);
        Mockito.when(userRepo.findByPrimaryId(eq(USER4_ID))).thenReturn(nf);

        List<Users> to = new ArrayList<>();
        to.add(thorodinson);
        Mockito.when(userRepo.findByPrimaryId(eq(USER5_ID))).thenReturn(to);
    }

    @Test
    public void testSendSuccess() throws UserDoesNotExistException, ParseException {
        svc.send(USER1, message);

        Mockito.verify(msgRepo, Mockito.times(1)).save(Mockito.any());
    }

    @Test(expected = UserDoesNotExistException.class)
    public void testSendFailure() throws UserDoesNotExistException, ParseException {
        Mockito.when(userRepo.findByUserId(message.getReceiver())).thenReturn(null);

        svc.send(USER1, message);

        Mockito.verify(msgRepo, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void testReadSuccessWithValues() throws ParseException, ReadMessageFailException {
        Messages messages1 = new Messages();
        messages1.setId("000000A");
        messages1.setSender(USER1_ID);
        messages1.setReceiver(USER2_ID);
        messages1.setSubject("Test 1");
        messages1.setContent("Message Content 1");
        messages1.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages2 = new Messages();
        messages2.setId("000000B");
        messages2.setSender(USER1_ID);
        messages2.setReceiver(USER2_ID);
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

        List<Message> message = svc.read(USER2);

        assertThat(message.get(0).getReceiver()).isEqualTo(USER2);
        assertThat(message.get(0).getSender()).isEqualTo(USER1);
        assertThat(message.get(0).getSubject()).isEqualTo(messages2.getSubject());
        assertThat(message.get(0).getContent()).isEqualTo(messages2.getContent());

        assertThat(message.get(1).getReceiver()).isEqualTo(USER2);
        assertThat(message.get(1).getSender()).isEqualTo(USER1);
        assertThat(message.get(1).getSubject()).isEqualTo(messages1.getSubject());
        assertThat(message.get(1).getContent()).isEqualTo(messages1.getContent());
    }

    @Test
    public void testReadSuccessButEmpty() throws ParseException, ReadMessageFailException {
        List<Messages> messages = new ArrayList<>();

        Mockito.when(msgRepo.findUnreadMessages(Mockito.anyString(), Mockito.any())).thenReturn(messages);

        List<Message> message = svc.read(USER2);

        assertThat(message.size()).isEqualTo(0);
    }

    @Test(expected = ReadMessageFailException.class)
    public void testReadSuccessWithException() throws ParseException, ReadMessageFailException {
        Messages messages1 = new Messages();
        messages1.setId("000000A");
        messages1.setSender(USER1_ID);
        messages1.setReceiver(USER2_ID);
        messages1.setSubject("Test 1");
        messages1.setContent("Message Content 1");
        messages1.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages2 = new Messages();
        messages2.setId("000000B");
        messages2.setSender(USER1_ID);
        messages2.setReceiver(USER2_ID);
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

        List<Message> message = svc.read(USER2);
    }

    @Test
    public void testListSentMessages() throws ParseException {
        Messages messages1 = new Messages();
        messages1.setId("000000A");
        messages1.setSender(USER1_ID);
        messages1.setReceiver(USER2_ID);
        messages1.setSubject("Test 1");
        messages1.setContent("Message Content 1");
        messages1.setStatus(EnumConstants.MessageStatus.READ.toString());

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages2 = new Messages();
        messages2.setId("000000B");
        messages2.setSender(USER1_ID);
        messages2.setReceiver(USER3_ID);
        messages2.setSubject("Test 2");
        messages2.setContent("Message Content 2");
        messages2.setStatus(EnumConstants.MessageStatus.READ.toString());

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages3 = new Messages();
        messages3.setId("000000C");
        messages3.setSender(USER1_ID);
        messages3.setReceiver(USER4_ID);
        messages3.setSubject("Test 3");
        messages3.setContent("Message Content 3");
        messages3.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Messages> messages = new ArrayList<>();
        messages.add(messages3);
        messages.add(messages2);
        messages.add(messages1);

        Mockito.when(msgRepo.findAllSentMessages(Mockito.anyString(), Mockito.any())).thenReturn(new PageImpl<>(messages));

        MessageList message = svc.listMessages(USER1, 1, HelperConstants.SENDER);

        assertThat(message.getMessages().get(0).getReceiver()).isEqualTo(USER4);
        assertThat(message.getMessages().get(0).getSender()).isEqualTo(USER1);
        assertThat(message.getMessages().get(0).getSubject()).isEqualTo(messages3.getSubject());
        assertThat(message.getMessages().get(0).getContent()).isEqualTo(messages3.getContent());

        assertThat(message.getMessages().get(1).getReceiver()).isEqualTo(USER3);
        assertThat(message.getMessages().get(1).getSender()).isEqualTo(USER1);
        assertThat(message.getMessages().get(1).getSubject()).isEqualTo(messages2.getSubject());
        assertThat(message.getMessages().get(1).getContent()).isEqualTo(messages2.getContent());

        assertThat(message.getMessages().get(2).getReceiver()).isEqualTo(USER2);
        assertThat(message.getMessages().get(2).getSender()).isEqualTo(USER1);
        assertThat(message.getMessages().get(2).getSubject()).isEqualTo(messages1.getSubject());
        assertThat(message.getMessages().get(2).getContent()).isEqualTo(messages1.getContent());
    }

    @Test
    public void testListReceivedMessages() throws ParseException {
        Messages messages1 = new Messages();
        messages1.setId("000000A");
        messages1.setSender(USER1_ID);
        messages1.setReceiver(USER5_ID);
        messages1.setSubject("I am Ironman");
        messages1.setContent("Yes, that's correct.");
        messages1.setStatus(EnumConstants.MessageStatus.READ.toString());

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages2 = new Messages();
        messages2.setId("000000B");
        messages2.setSender(USER2_ID);
        messages2.setReceiver(USER5_ID);
        messages2.setSubject("Super Soldier Serum");
        messages2.setContent("That's where my abilities came from.");
        messages2.setStatus(EnumConstants.MessageStatus.READ.toString());

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Messages messages3 = new Messages();
        messages3.setId("000000C");
        messages3.setSender(USER4_ID);
        messages3.setReceiver(USER5_ID);
        messages3.setSubject("S.H.I.E.L.D");
        messages3.setContent("I would like to invite you to join the Avengers Initiative.");
        messages3.setStatus(EnumConstants.MessageStatus.UNREAD.toString());

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        messages3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Messages> messages = new ArrayList<>();
        messages.add(messages3);
        messages.add(messages2);
        messages.add(messages1);

        Mockito.when(msgRepo.findAllReceivedMessages(Mockito.anyString(), Mockito.any())).thenReturn(new PageImpl<>(messages));

        MessageList message = svc.listMessages(USER5, 1, HelperConstants.RECEIVER);

        assertThat(message.getMessages().get(0).getReceiver()).isEqualTo(USER5);
        assertThat(message.getMessages().get(0).getSender()).isEqualTo(USER4);
        assertThat(message.getMessages().get(0).getSubject()).isEqualTo(messages3.getSubject());
        assertThat(message.getMessages().get(0).getContent()).isEqualTo(messages3.getContent());

        assertThat(message.getMessages().get(1).getReceiver()).isEqualTo(USER5);
        assertThat(message.getMessages().get(1).getSender()).isEqualTo(USER2);
        assertThat(message.getMessages().get(1).getSubject()).isEqualTo(messages2.getSubject());
        assertThat(message.getMessages().get(1).getContent()).isEqualTo(messages2.getContent());

        assertThat(message.getMessages().get(2).getReceiver()).isEqualTo(USER5);
        assertThat(message.getMessages().get(2).getSender()).isEqualTo(USER1);
        assertThat(message.getMessages().get(2).getSubject()).isEqualTo(messages1.getSubject());
        assertThat(message.getMessages().get(2).getContent()).isEqualTo(messages1.getContent());
    }

    @Test(expected = NullPointerException.class)
    public void testListMessagesNull() {
        Mockito.when(msgRepo.findAllReceivedMessages(Mockito.anyString(), Mockito.any())).thenReturn(null);

        MessageList message = svc.listMessages(USER5, 1, HelperConstants.RECEIVER);
    }

    @Test
    public void testCountPredictionForTheDay() {
        Mockito.when(msgRepo.messageCountByDateDuration(Mockito.any(), Mockito.any())).thenReturn(2406);

        String message = svc.messageCountPrediction(HelperConstants.DAY);
        String expected = String.format("Predicted message count to receive for the day is %d", 171);

        assertThat(message).isEqualTo(expected);
    }

    @Test
    public void testCountPredictionForTheWeek() {
        Mockito.when(msgRepo.messageCountByDateDuration(Mockito.any(), Mockito.any())).thenReturn(5875);

        String message = svc.messageCountPrediction(HelperConstants.WEEK);
        String expected = String.format("Predicted message count to receive for the week is %d", 1468);

        assertThat(message).isEqualTo(expected);
    }

}
