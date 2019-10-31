package com.aldren.messaging.unit.controller;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.controller.MessageController;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.service.MessageService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    private static final String SENDER = "tonystark";
    private static final String RECEIVER = "steverogers";
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
        message1.setSender(SENDER);
        message1.setReceiver(RECEIVER);
        message1.setSubject("Test 1");
        message1.setContent("Message Content 1");

        String date1 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message1.setSentDate(DateUtils.parseDate(date1, HelperConstants.TIMESTAMP_FORMAT));

        Message message2 = new Message();
        message2.setSender(SENDER);
        message2.setReceiver(RECEIVER);
        message2.setSubject("Test 2");
        message2.setContent("Message Content 2");

        String date2 = DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT);
        message2.setSentDate(DateUtils.parseDate(date2, HelperConstants.TIMESTAMP_FORMAT));

        List<Message> messages = new ArrayList<>();
        messages.add(message2);
        messages.add(message1);

        Mockito.when(svc.read(eq(RECEIVER))).thenReturn(messages);

        mvc.perform(get("/api/v1/message/read")
                .header(X_USER_HEADER, RECEIVER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].subject", is("Test 2")))
                .andExpect(jsonPath("$[0].content", is("Message Content 2")))
                .andExpect(jsonPath("$[1].subject", is("Test 1")))
                .andExpect(jsonPath("$[1].content", is("Message Content 1")));
    }

}
