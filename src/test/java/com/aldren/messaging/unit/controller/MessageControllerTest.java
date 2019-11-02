package com.aldren.messaging.unit.controller;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.controller.MessageController;
import com.aldren.messaging.exception.ReadMessageFailException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    private static final String USER1 = "tonystark";
    private static final String USER2 = "steverogers";
    private static final String USER3 = "mariahill";
    private static final String USER4 = "nickfury";
    private static final String USER5 = "thorodinson";
    private static final String X_USER_HEADER = "X-User";
    public MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private MessageService svc;

    @Before
    public void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void readTest() throws Exception {
        Message message1 = new Message();
        message1.setSender(USER1);
        message1.setReceiver(USER2);
        message1.setSubject("Test 1");
        message1.setContent("Message Content 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setSender(USER5);
        message2.setReceiver(USER2);
        message2.setSubject("Test 2");
        message2.setContent("Message Content 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message2);
        messages.add(message1);

        Mockito.when(svc.read(eq(USER2))).thenReturn(messages);

        mvc.perform(get("/api/v1/message/read")
                .header(X_USER_HEADER, USER2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].subject", is("Test 2")))
                .andExpect(jsonPath("$[0].content", is("Message Content 2")))
                .andExpect(jsonPath("$[0].sender", is(USER5)))
                .andExpect(jsonPath("$[1].subject", is("Test 1")))
                .andExpect(jsonPath("$[1].content", is("Message Content 1")))
                .andExpect(jsonPath("$[1].sender", is(USER1)));
    }

    @Test
    public void readTestBadRequestResponse() throws Exception {
        Mockito.when(svc.read(eq(USER2))).thenThrow(ReadMessageFailException.class);

        mvc.perform(get("/api/v1/message/read")
                .header(X_USER_HEADER, USER2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sendTest() throws Exception {
        Message message = new Message();
        message.setReceiver(USER3);
        message.setSubject("Avengers Initiative");
        message.setContent("Let's start the initiative, and start gathering members.");

        mvc.perform(post("/api/v1/message/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message))
                .header(X_USER_HEADER, USER4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.message.receiver", is(USER3)));

        Mockito.verify(svc, Mockito.times(1)).send(eq(USER4), Mockito.any());
    }

    @Test
    public void sendTestUserDoesNotExistException() throws Exception {
        Message message = new Message();
        message.setReceiver("USER3");
        message.setSubject("Avengers Initiative");
        message.setContent("Let's start the initiative, and start gathering members.");

        Mockito.doThrow(UserDoesNotExistException.class).when(svc).send(Mockito.anyString(), Mockito.any());

        mvc.perform(post("/api/v1/message/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message))
                .header(X_USER_HEADER, USER4))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.NOT_FOUND.name())));
    }

    @Test
    public void sendTestParseException() throws Exception {
        Message message = new Message();
        message.setReceiver("USER3");
        message.setSubject("Avengers Initiative");
        message.setContent("Let's start the initiative, and start gathering members.");

        Mockito.doThrow(ParseException.class).when(svc).send(Mockito.anyString(), Mockito.any());

        mvc.perform(post("/api/v1/message/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message))
                .header(X_USER_HEADER, USER4))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.INTERNAL_SERVER_ERROR.name())));
    }

    private String convertObject(Message message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);
    }

}
