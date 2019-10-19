package com.aldren.messaging.controller;

import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.Response;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @PostMapping("/message/send")
    public Response send(@RequestBody Message message) {
        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                .status(HttpStatus.OK.value())
                .information(HttpStatus.OK.toString())
                .message(message)
                .build();
    }

}
