package com.aldren.messaging.controller;

import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.Response;
import com.aldren.messaging.document.Users;
import com.aldren.messaging.repository.UsersRepository;
import com.aldren.messaging.service.MessageService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    private MessageService svc;

    @PostMapping("/message/send")
    public Response send(HttpServletRequest request, @RequestBody Message message) throws UserDoesNotExistException {
        String user = request.getHeader("X-User");

        svc.send(user, message);

        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                .status(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .message(message)
                .build();
    }

}
