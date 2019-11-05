package com.aldren.messaging.handler;

import com.aldren.messaging.constants.HelperConstants;
import com.aldren.messaging.exception.BadRequestException;
import com.aldren.messaging.exception.ReadMessageFailException;
import com.aldren.messaging.exception.UserDoesNotExistException;
import com.aldren.messaging.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Response handleBadRequestException(Exception e, WebRequest u) {
        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.BAD_REQUEST.value())
                .description(HttpStatus.BAD_REQUEST.name())
                .information(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({UserDoesNotExistException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    Response handleNotFoundException(Exception e, WebRequest u) {
        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.NOT_FOUND.value())
                .description(HttpStatus.NOT_FOUND.name())
                .information(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({ParseException.class, NullPointerException.class, ReadMessageFailException.class, })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    Response handleInternalServerException(Exception e, WebRequest u) {
        return Response.builder()
                .timestamp(DateFormatUtils.format(new Date(), HelperConstants.TIMESTAMP_FORMAT))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .description(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .information(e.getLocalizedMessage())
                .build();
    }

}
