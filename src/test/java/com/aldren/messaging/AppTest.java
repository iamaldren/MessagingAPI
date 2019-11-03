package com.aldren.messaging;

import com.aldren.messaging.document.Messages;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.repository.UsersRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration test for the App
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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

    private static final String ACTIVE_STATUS = "ACTIVE";

    @Before
    public void setup() throws Exception {
        if(!IS_INITIALIZED) {
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
    public void shouldAnswerWithTrue() {
        Users test = usersRepository.findByUserId(TONY_STARK);
        assertThat(test.getUserId()).isEqualTo(TONY_STARK);
    }
}
