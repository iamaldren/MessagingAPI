package com.aldren.messaging.controller;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.exception.BadRequestException;
import com.aldren.messaging.exception.MessageDoesNotExistException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Message;
import com.aldren.messaging.model.MessageList;
import com.aldren.messaging.model.Response;
import com.aldren.messaging.service.MessageService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    private MessageService svc;

    private Set<String> forecastValues;

    @PostMapping("/messages")
    public Response send(@RequestBody Message message) throws UserDoesNotExistException, ParseException {
        svc.send(message);

        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .message(message)
                .build();
    }

    @GetMapping("/messages/{messageId}")
    public Message read(@PathVariable String messageId) throws MessageDoesNotExistException {
        return svc.read(messageId);
    }

    @GetMapping("/messages")
    public ResponseEntity sent(@RequestParam("sender") Optional<String> sender,
                               @RequestParam("receiver") Optional<String> receiver,
                               @RequestParam("forecast") Optional<String> forecast,
                               @RequestParam("page") Optional<Integer> page) throws BadRequestException {

        if(forecast.isPresent()) {
            if (!forecastValues.contains(forecast.get())) {
                throw new BadRequestException("Type entered is not supported. Day/Week computation are the currently supported count prediction.");
            }

            return ResponseEntity.of(Optional.of(Response.builder()
                    .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                    .status(HttpStatus.OK.value())
                    .description(HttpStatus.OK.name())
                    .information(svc.messageCountPrediction(forecast.get()))
                    .build()));
        }

        int pagination = 0;
        if(page.isPresent() && page.get() > 1) {
            pagination = page.get() - 1;
        }

        String user = "";
        String role = "";

        if(sender.isPresent()) {
            user = sender.get();
            role = HelperConstants.SENDER;
        } else if(receiver.isPresent()) {
            user = receiver.get();
            role = HelperConstants.RECEIVER;
        } else {
            throw new BadRequestException("Atleast 1 of the params[sender, receiver] must be present for this endpoint.");
        }

        return ResponseEntity.of(Optional.of(svc.listMessages(user, pagination, role)));
    }

    @PostConstruct
    public void getEnums() {
        forecastValues = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        forecastValues.add(HelperConstants.DAY);
        forecastValues.add(HelperConstants.WEEK);
    }

}
