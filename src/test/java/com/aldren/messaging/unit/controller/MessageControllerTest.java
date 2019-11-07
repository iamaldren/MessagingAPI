package com.aldren.messaging.unit.controller;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.controller.MessageController;
import com.aldren.messaging.exception.MessageDoesNotExistException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.MessageList;
import com.aldren.messaging.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    private static final String USER1 = "tonystark";
    private static final String USER2 = "steverogers";
    private static final String USER3 = "mariahill";
    private static final String USER4 = "nickfury";
    private static final String USER5 = "thorodinson";
    private static final String USER1_FN = "Tony Stark";
    private static final String USER2_FN = "Steve Rogers";
    private static final String USER3_FN = "Maria Hill";
    private static final String USER4_FN = "Nick Fury";
    private static final String USER5_FN = "Thor Odinson";
    private static final String X_USER_HEADER = "X-User";
    public MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private MessageService svc;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testReadMessageDetail() throws Exception {
        Message message1 = new Message();
        message1.setSender(USER1_FN);
        message1.setReceiver(USER2_FN);
        message1.setSubject("Test 1");
        message1.setContent("Message Content 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Mockito.when(svc.read(eq("000000A"))).thenReturn(message1);

        mvc.perform(get("/api/v1/messages/000000A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject", is("Test 1")))
                .andExpect(jsonPath("$.content", is("Message Content 1")))
                .andExpect(jsonPath("$.sender", is(USER1_FN)))
                .andExpect(jsonPath("$.receiver", is(USER2_FN)));
    }

    @Test
    public void testReadMessageDetailBadRequestResponse() throws Exception {
        Mockito.when(svc.read(eq("000000B"))).thenThrow(MessageDoesNotExistException.class);

        mvc.perform(get("/api/v1/messages/000000B"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.NOT_FOUND.name())));
    }

    @Test
    public void testSendMessage() throws Exception {
        Message message = new Message();
        message.setSender(USER4);
        message.setReceiver(USER3);
        message.setSubject("Avengers Initiative");
        message.setContent("Let's start the initiative, and start gathering members.");

        mvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.message.sender", is(USER4)))
                .andExpect(jsonPath("$.message.receiver", is(USER3)));

        Mockito.verify(svc, Mockito.times(1)).send(Mockito.any());
    }

    @Test
    public void testSendMessageUserDoesNotExistException() throws Exception {
        Message message = new Message();
        message.setSender(USER5);
        message.setReceiver("caroldanvers");
        message.setSubject("Avengers Initiative");
        message.setContent("Let's start the initiative, and start gathering members.");

        Mockito.doThrow(UserDoesNotExistException.class).when(svc).send(Mockito.any());

        mvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.NOT_FOUND.name())));
    }

    @Test
    public void testSendMessageParseException() throws Exception {
        Message message = new Message();
        message.setSender(USER1);
        message.setReceiver(USER2);
        message.setSubject("Avengers Initiative");
        message.setContent("Let's start the initiative, and start gathering members.");

        Mockito.doThrow(ParseException.class).when(svc).send(Mockito.any());

        mvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.INTERNAL_SERVER_ERROR.name())));
    }

    @Test
    public void testListAllMessagesByAUser() throws Exception {
        Message message1 = new Message();
        message1.setId("000001A");
        message1.setReceiver(USER3_FN);
        message1.setSubject("Test 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setId("000001B");
        message2.setReceiver(USER4_FN);
        message2.setSubject("Test 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Message message3 = new Message();
        message3.setId("000001C");
        message3.setReceiver(USER5_FN);
        message3.setSubject("Test 3");

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message3);
        messages.add(message2);
        messages.add(message1);

        MessageList messageList = new MessageList();
        messageList.setTotalPage(1);
        messageList.setMessages(messages);

        Mockito.when(svc.listMessages(eq(USER1), eq(0), eq(HelperConstants.SENDER))).thenReturn(messageList);

        mvc.perform(get("/api/v1/messages?page=1&sender=" + USER1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages[0].subject", is("Test 3")))
                .andExpect(jsonPath("$.messages[0].receiver", is(USER5_FN)))
                .andExpect(jsonPath("$.messages[1].subject", is("Test 2")))
                .andExpect(jsonPath("$.messages[1].receiver", is(USER4_FN)))
                .andExpect(jsonPath("$.messages[2].subject", is("Test 1")))
                .andExpect(jsonPath("$.messages[2].receiver", is(USER3_FN)));
    }

    @Test
    public void testListAllMessagesByAUserPageLessThan1() throws Exception {
        Message message1 = new Message();
        message1.setId("000001A");
        message1.setReceiver(USER3_FN);
        message1.setSubject("Test 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setId("000001B");
        message2.setReceiver(USER4_FN);
        message2.setSubject("Test 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Message message3 = new Message();
        message3.setId("000001C");
        message3.setReceiver(USER5_FN);
        message3.setSubject("Test 3");

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message3);
        messages.add(message2);
        messages.add(message1);

        MessageList messageList = new MessageList();
        messageList.setTotalPage(1);
        messageList.setMessages(messages);

        Mockito.when(svc.listMessages(eq(USER1), eq(0), eq(HelperConstants.SENDER))).thenReturn(messageList);

        mvc.perform(get("/api/v1/messages?page=-1&sender=" + USER1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages[0].subject", is("Test 3")))
                .andExpect(jsonPath("$.messages[0].receiver", is(USER5_FN)))
                .andExpect(jsonPath("$.messages[1].subject", is("Test 2")))
                .andExpect(jsonPath("$.messages[1].receiver", is(USER4_FN)))
                .andExpect(jsonPath("$.messages[2].subject", is("Test 1")))
                .andExpect(jsonPath("$.messages[2].receiver", is(USER3_FN)));
    }

    @Test
    public void testListAllMessagesByAUserPageParamMissing() throws Exception {
        Message message1 = new Message();
        message1.setId("000001A");
        message1.setReceiver(USER3_FN);
        message1.setSubject("Test 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setId("000001B");
        message2.setReceiver(USER4_FN);
        message2.setSubject("Test 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Message message3 = new Message();
        message3.setId("000001C");
        message3.setReceiver(USER5_FN);
        message3.setSubject("Test 3");

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message3);
        messages.add(message2);
        messages.add(message1);

        MessageList messageList = new MessageList();
        messageList.setTotalPage(1);
        messageList.setMessages(messages);

        Mockito.when(svc.listMessages(eq(USER1), eq(0), eq(HelperConstants.SENDER))).thenReturn(messageList);

        mvc.perform(get("/api/v1/messages?sender=" + USER1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages[0].subject", is("Test 3")))
                .andExpect(jsonPath("$.messages[0].receiver", is(USER5_FN)))
                .andExpect(jsonPath("$.messages[1].subject", is("Test 2")))
                .andExpect(jsonPath("$.messages[1].receiver", is(USER4_FN)))
                .andExpect(jsonPath("$.messages[2].subject", is("Test 1")))
                .andExpect(jsonPath("$.messages[2].receiver", is(USER3_FN)));
    }

    @Test
    public void testListAllMessagesByAUserMissingParam() throws Exception {
        mvc.perform(get("/api/v1/messages?page=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.BAD_REQUEST.name())));
    }

    @Test
    public void testListAllMessagesForAUser() throws Exception {
        Message message1 = new Message();
        message1.setId("000002A");
        message1.setSender(USER1_FN);
        message1.setSubject("Test 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setId("000002B");
        message2.setSender(USER5_FN);
        message2.setSubject("Test 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Message message3 = new Message();
        message3.setId("000002C");
        message3.setSender(USER4_FN);
        message3.setSubject("Test 3");

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message3);
        messages.add(message2);
        messages.add(message1);

        MessageList messageList = new MessageList();
        messageList.setTotalPage(1);
        messageList.setMessages(messages);

        Mockito.when(svc.listMessages(eq(USER2), eq(0), eq(HelperConstants.RECEIVER))).thenReturn(messageList);

        mvc.perform(get("/api/v1/messages?page=1&receiver=" + USER2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages[0].subject", is("Test 3")))
                .andExpect(jsonPath("$.messages[0].sender", is(USER4_FN)))
                .andExpect(jsonPath("$.messages[1].subject", is("Test 2")))
                .andExpect(jsonPath("$.messages[1].sender", is(USER5_FN)))
                .andExpect(jsonPath("$.messages[2].subject", is("Test 1")))
                .andExpect(jsonPath("$.messages[2].sender", is(USER1_FN)));
    }

    @Test
    public void testListAllMessagesForAUserPageLessThan1() throws Exception {
        Message message1 = new Message();
        message1.setId("000002A");
        message1.setSender(USER1_FN);
        message1.setSubject("Test 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setId("000002B");
        message2.setSender(USER5_FN);
        message2.setSubject("Test 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Message message3 = new Message();
        message3.setId("000002C");
        message3.setSender(USER4_FN);
        message3.setSubject("Test 3");

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message3);
        messages.add(message2);
        messages.add(message1);

        MessageList messageList = new MessageList();
        messageList.setTotalPage(1);
        messageList.setMessages(messages);

        Mockito.when(svc.listMessages(eq(USER2), eq(0), eq(HelperConstants.RECEIVER))).thenReturn(messageList);

        mvc.perform(get("/api/v1/messages?page=-1&receiver=" + USER2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages[0].subject", is("Test 3")))
                .andExpect(jsonPath("$.messages[0].sender", is(USER4_FN)))
                .andExpect(jsonPath("$.messages[1].subject", is("Test 2")))
                .andExpect(jsonPath("$.messages[1].sender", is(USER5_FN)))
                .andExpect(jsonPath("$.messages[2].subject", is("Test 1")))
                .andExpect(jsonPath("$.messages[2].sender", is(USER1_FN)));
    }

    @Test
    public void testListAllMessagesForAUserPageParamMissing() throws Exception {
        Message message1 = new Message();
        message1.setId("000002A");
        message1.setSender(USER1_FN);
        message1.setSubject("Test 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setId("000002B");
        message2.setSender(USER5_FN);
        message2.setSubject("Test 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        Message message3 = new Message();
        message3.setId("000002C");
        message3.setSender(USER4_FN);
        message3.setSubject("Test 3");

        String date3 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message3.setSentDate(DateUtils.parseDate(date3, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message3);
        messages.add(message2);
        messages.add(message1);

        MessageList messageList = new MessageList();
        messageList.setTotalPage(1);
        messageList.setMessages(messages);

        Mockito.when(svc.listMessages(eq(USER2), eq(0), eq(HelperConstants.RECEIVER))).thenReturn(messageList);

        mvc.perform(get("/api/v1/messages?receiver=" + USER2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(3)))
                .andExpect(jsonPath("$.messages[0].subject", is("Test 3")))
                .andExpect(jsonPath("$.messages[0].sender", is(USER4_FN)))
                .andExpect(jsonPath("$.messages[1].subject", is("Test 2")))
                .andExpect(jsonPath("$.messages[1].sender", is(USER5_FN)))
                .andExpect(jsonPath("$.messages[2].subject", is("Test 1")))
                .andExpect(jsonPath("$.messages[2].sender", is(USER1_FN)));
    }

    @Test
    public void testListAllMessagesForAUserMissingParam() throws Exception {
        mvc.perform(get("/api/v1/messages?page=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.BAD_REQUEST.name())));
    }

    @Test
    public void testPredictMessageForTheDay() throws Exception {
        String message = "Predicted message count to receive for the day is 171";
        Mockito.when(svc.messageCountPrediction(Mockito.anyString())).thenReturn(message);

        mvc.perform(get("/api/v1/messages?forecast=Day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.information", is(message)));
    }

    @Test
    public void testPredictMessageForTheWeek() throws Exception {
        String message = "Predicted message count to receive for the week is 1468";
        Mockito.when(svc.messageCountPrediction(Mockito.anyString())).thenReturn(message);

        mvc.perform(get("/api/v1/messages?forecast=WEEK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.information", is(message)));
    }

    @Test
    public void testPredictMessageThrowBadRequestException() throws Exception {
        mvc.perform(get("/api/v1/messages?forecast=month"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.BAD_REQUEST.name())));
    }

    private String convertObject(Message message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);
    }

}
