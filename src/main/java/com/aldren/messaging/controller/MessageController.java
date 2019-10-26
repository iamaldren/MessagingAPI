package com.aldren.messaging.controller;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.exception.BadRequestException;
import com.aldren.messaging.exception.ReadMessageFailException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.Response;
import com.aldren.messaging.service.MessageService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    private MessageService svc;

    private Set<String> typeValues;

    @GetMapping("/message/read")
    public List<Message> read(HttpServletRequest request) throws ReadMessageFailException {
        String user = request.getHeader("X-User");
        return svc.read(user);
    }

    @PostMapping("/message/send")
    public Response send(HttpServletRequest request, @RequestBody Message message) throws UserDoesNotExistException, ParseException {
        String user = request.getHeader("X-User");

        svc.send(user, message);

        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .message(message)
                .build();
    }

    @GetMapping("/message/sent")
    public List<Message> sent(HttpServletRequest request, @RequestParam int page) throws UserDoesNotExistException, ParseException {
        String user = request.getHeader("X-User");
        return svc.listMessages(user, page, HelperConstants.SENDER);
    }

    @GetMapping("/message/receive")
    public List<Message> receive(HttpServletRequest request, @RequestParam int page) throws UserDoesNotExistException, ParseException {
        String user = request.getHeader("X-User");
        return svc.listMessages(user, page, HelperConstants.RECEIVER);
    }

    @GetMapping("/message/predict")
    public Response predict(@RequestParam String type) throws BadRequestException {
        if(!typeValues.contains(type)) {
            throw new BadRequestException("Type entered is not supported. Day/Week computation are the currently supported count prediction.");
        }

        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .information(svc.messageCountPrediction(type))
                .build();
    }

    @PostConstruct
    public void getEnums() {
        typeValues = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        typeValues.add(HelperConstants.DAY);
        typeValues.add(HelperConstants.WEEK);
    }

}
