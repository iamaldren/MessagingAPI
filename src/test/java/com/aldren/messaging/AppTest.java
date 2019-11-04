package com.aldren.messaging;

import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.repository.UsersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the App
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class AppTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UsersRepository usersRepository;

    private static boolean IS_INITIALIZED = false;

    private static final String TONY_STARK = "tonystark";
    private static final String STEVE_ROGERS = "steverogers";
    private static final String NICK_FURY = "nickfury";
    private static final String MARIA_HILL = "mariahill";
    private static final String THOR_ODINSON = "thorodinson";

    private static final String ROLE_USER = "User";
    private static final String X_USER_HEADER = "X-User";

    private static final String ACTIVE_STATUS = "ACTIVE";

    @Before
    public void setup() throws Exception {
        if(!IS_INITIALIZED) {
            log.info(">>>>>>>>>>>>>>>>>>>>> SETUP <<<<<<<<<<<<<<<<<<<<<<<<<");
            mongoTemplate.dropCollection(Users.class);
            mongoTemplate.dropCollection(Messages.class);

            Users tonystark = new Users();
            tonystark.setUserId(TONY_STARK);
            tonystark.setFirstName("Tony");
            tonystark.setLastName("Start");
            tonystark.setRole(ROLE_USER);
            tonystark.setStatus(ACTIVE_STATUS);

            Users steverogers = new Users();
            steverogers.setUserId(STEVE_ROGERS);
            steverogers.setFirstName("Steve");
            steverogers.setLastName("Rogers");
            steverogers.setRole(ROLE_USER);
            steverogers.setStatus(ACTIVE_STATUS);

            Users nickfury = new Users();
            nickfury.setUserId(NICK_FURY);
            nickfury.setFirstName("Nick");
            nickfury.setLastName("Fury");
            nickfury.setRole(ROLE_USER);
            nickfury.setStatus(ACTIVE_STATUS);

            Users mariahill = new Users();
            mariahill.setUserId(MARIA_HILL);
            mariahill.setFirstName("Maria");
            mariahill.setLastName("Hill");
            mariahill.setRole(ROLE_USER);
            mariahill.setStatus(ACTIVE_STATUS);

            Users thorodinson = new Users();
            thorodinson.setUserId(THOR_ODINSON);
            thorodinson.setFirstName("Thor");
            thorodinson.setLastName("Odinson");
            thorodinson.setRole(ROLE_USER);
            thorodinson.setStatus(ACTIVE_STATUS);

            List<Users> users = new ArrayList<>();
            users.add(tonystark);
            users.add(steverogers);
            users.add(nickfury);
            users.add(mariahill);
            users.add(thorodinson);

            mongoTemplate.createCollection(Users.class);
            mongoTemplate.createCollection(Messages.class);

            usersRepository.saveAll(users);

            mockMvc.perform(get("/api/v1/test"));
            IS_INITIALIZED = true;
        }
    }

    @Test
    public void testReadAndSendEndpoint() throws Exception {
        String subject1 = "Avengers Initiative";
        String content1 = "Let's start the initiative, and start gathering members.";

        Message message1 = new Message();
        message1.setReceiver(STEVE_ROGERS);
        message1.setSubject(subject1);
        message1.setContent(content1);

        mockMvc.perform(post("/api/v1/message/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message1))
                .header(X_USER_HEADER, TONY_STARK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.message.receiver", is(STEVE_ROGERS)))
                .andExpect(jsonPath("$.message.sender", is(TONY_STARK)));

        String subject2 = "Avengers";
        String content2 = "I heard there's an initiative to gather superpowered beings as Earth's protectors. I wanna join!";

        Message message2 = new Message();
        message2.setReceiver(STEVE_ROGERS);
        message2.setSubject(subject2);
        message2.setContent(content2);

        mockMvc.perform(post("/api/v1/message/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message2))
                .header(X_USER_HEADER, THOR_ODINSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.message.receiver", is(STEVE_ROGERS)))
                .andExpect(jsonPath("$.message.sender", is(THOR_ODINSON)));

        mockMvc.perform(get("/api/v1/message/read")
                .header(X_USER_HEADER, STEVE_ROGERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].subject", is(subject2)))
                .andExpect(jsonPath("$[0].content", is(content2)))
                .andExpect(jsonPath("$[0].sender", is(THOR_ODINSON)))
                .andExpect(jsonPath("$[0].receiver", is(STEVE_ROGERS)))
                .andExpect(jsonPath("$[1].subject", is(subject1)))
                .andExpect(jsonPath("$[1].content", is(content1)))
                .andExpect(jsonPath("$[1].sender", is(TONY_STARK)))
                .andExpect(jsonPath("$[1].receiver", is(STEVE_ROGERS)));
    }

    private String convertObject(Message message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);
    }

}
