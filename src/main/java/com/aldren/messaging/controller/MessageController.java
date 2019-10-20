package com.aldren.messaging.controller;

import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.Response;
import com.aldren.messaging.model.Users;
import com.aldren.messaging.repository.UsersRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    private UsersRepository repo;

    @PostMapping("/message/send")
    public Response send(@RequestBody Message message) {
        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                .status(HttpStatus.OK.value())
                .information(HttpStatus.OK.name())
                .message(message)
                .build();
    }

    @GetMapping("/users")
    public List<Users> getUsers() {
        return repo.findAll();
    }

}
