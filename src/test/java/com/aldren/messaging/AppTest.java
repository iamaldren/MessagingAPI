package com.aldren.messaging;

import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.repository.UsersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the App
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    private static final String TONY_STARK_FN = "Tony Stark";
    private static final String STEVE_ROGERS_FN = "Steve Rogers";
    private static final String NICK_FURY_FN = "Nick Fury";
    private static final String MARIA_HILL_FN = "Maria Hill";
    private static final String THOR_ODINSON_FN = "Thor Odinson";

    private static final String ROLE_USER = "User";

    private static final String ACTIVE_STATUS = "ACTIVE";

    private Random randomGenerator = new Random();

    private String[] users = {TONY_STARK, STEVE_ROGERS, MARIA_HILL, NICK_FURY, THOR_ODINSON};

    @BeforeEach
    public void setup() throws Exception {
        if(!IS_INITIALIZED) {
            log.info(">>>>>>>>>>>>>>>>>>>>> SETUP <<<<<<<<<<<<<<<<<<<<<<<<<");

            mongoTemplate.dropCollection(Users.class);
            mongoTemplate.dropCollection(Messages.class);

            Users tonystark = new Users();
            tonystark.setUserId(TONY_STARK);
            tonystark.setFirstName("Tony");
            tonystark.setLastName("Stark");
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
    @Order(1)
    public void testReadAndSendEndpoint() throws Exception {
        String subject1 = "Avengers Initiative";
        String content1 = "Let's start the initiative, and start gathering members.";

        Message message1 = new Message();
        message1.setSender(TONY_STARK);
        message1.setReceiver(STEVE_ROGERS);
        message1.setSubject(subject1);
        message1.setContent(content1);

        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.message.receiver", is(STEVE_ROGERS)))
                .andExpect(jsonPath("$.message.sender", is(TONY_STARK)));

        MvcResult firstPage = mockMvc.perform(get("/api/v1/messages?page=1&sender=" + TONY_STARK)).andReturn();

        String response = firstPage.getResponse().getContentAsString();
        JsonObject fpObject = new JsonObject(response);

        String messageId = fpObject.getJsonArray("messages").getJsonObject(0).getString("id");

        mockMvc.perform(get("/api/v1/messages/" + messageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender", is(TONY_STARK_FN)))
                .andExpect(jsonPath("$.receiver", is(STEVE_ROGERS_FN)))
                .andExpect(jsonPath("$.subject", is(subject1)))
                .andExpect(jsonPath("$.content", is(content1)));
    }

    @Test
    @Order(2)
    public void testAllSentMessages() throws Exception {
        int userNum = randomGenerator.nextInt(5);
        String user = this.users[userNum];

        String subject = "Test Subject (Sender)";
        String content = "Testing All Sent messages endpoint.";

        /**
         * This part is to make sure that there is atleast
         * 1 page present for the selected random user.
         * For the purpose of assertion in the below test.
         */
        Message message1 = new Message();
        message1.setSender(user);
        message1.setReceiver(this.users[userNum == 4 ? userNum - 1 : userNum + 1]);
        message1.setSubject(subject);
        message1.setContent(content);

        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message1)));

        MvcResult firstPage = mockMvc.perform(get("/api/v1/messages?page=1&sender=" + user)).andReturn();

        String response = firstPage.getResponse().getContentAsString();
        JsonObject fpObject = new JsonObject(response);

        assertThat(fpObject.getJsonArray("messages")).isNotEmpty();
        assertThat(fpObject.getJsonArray("messages").getJsonObject(0).getString("id")).isNotNull();
        assertThat(fpObject.getJsonArray("messages").getJsonObject(0).getString("subject")).isNotNull();
        assertThat(fpObject.getJsonArray("messages").getJsonObject(0).getString("receiver")).isNotNull();

        /**
         * If there is only 1 page, no point in getting the last page.
         */
        int lastPage = fpObject.getInteger("totalPage");
        if(lastPage > 1) {
            MvcResult LastPage = mockMvc.perform(get("/api/v1/messages?page=" + lastPage + "&sender=" + user)).andReturn();

            response = LastPage.getResponse().getContentAsString();
            JsonObject lpObject = new JsonObject(response);

            assertThat(lpObject.getJsonArray("messages")).isNotEmpty();
        }
    }

    @Test
    @Order(3)
    public void testAllReceiveMessages() throws Exception {
        int userNum = randomGenerator.nextInt(5);
        String user = this.users[userNum];

        String subject = "Test Subject (Receiver)";
        String content = "Testing All Receive messages endpoint.";

        Message message1 = new Message();
        message1.setSender(this.users[userNum == 4 ? userNum - 1 : userNum + 1]);
        message1.setReceiver(user);
        message1.setSubject(subject);
        message1.setContent(content);

        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObject(message1)));

        MvcResult firstPage = mockMvc.perform(get("/api/v1/messages?page=1&receiver=" + user)).andReturn();

        String response = firstPage.getResponse().getContentAsString();
        JsonObject fpObject = new JsonObject(response);

        assertThat(fpObject.getJsonArray("messages")).isNotEmpty();
        assertThat(fpObject.getJsonArray("messages").getJsonObject(0).getString("id")).isNotNull();
        assertThat(fpObject.getJsonArray("messages").getJsonObject(0).getString("subject")).isNotNull();
        assertThat(fpObject.getJsonArray("messages").getJsonObject(0).getString("sender")).isNotNull();

        int lastPage = fpObject.getInteger("totalPage");
        if(lastPage > 1) {
            MvcResult LastPage = mockMvc.perform(get("/api/v1/messages?page=" + lastPage + "&receiver=" + user)).andReturn();

            response = LastPage.getResponse().getContentAsString();
            JsonObject lpObject = new JsonObject(response);

            assertThat(lpObject.getJsonArray("messages")).isNotEmpty();
        }
    }

    @Test
    @Order(4)
    public void testPredictedMessageCountForTheDay() throws Exception {
        String message = "Predicted message count to receive for the day is 22";

        mockMvc.perform(get("/api/v1/messages?forecast=Day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.information", is(message)));
    }

    @Test
    @Order(5)
    public void testPredictedMessageCountForTheWeek() throws Exception {
        String message = "Predicted message count to receive for the week is 116";

        mockMvc.perform(get("/api/v1/messages?forecast=week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.description", is(HttpStatus.OK.name())))
                .andExpect(jsonPath("$.information", is(message)));
    }

    private String convertObject(Message message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);
    }

}
