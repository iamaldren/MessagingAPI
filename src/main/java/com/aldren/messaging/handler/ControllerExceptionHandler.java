package com.aldren.messaging.handler;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Response;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserDoesNotExistException.class})
    public Response handleNotFoundException(Exception e, WebRequest u) {
        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.NOT_FOUND.value())
                .description(HttpStatus.NOT_FOUND.name())
                .information(e.getLocalizedMessage())
                .build();
    }

}
