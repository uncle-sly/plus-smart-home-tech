package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class ErrorResponseBuild {

    public static ErrorResponse getErrorResponse(RuntimeException e, HttpStatus status) {

        return ErrorResponse.builder()
                .cause(e.getCause())
                .stackTrace(Arrays.asList(e.getStackTrace()))
                .httpStatus(status)
                .userMessage(e.getMessage())
                .message(status.getReasonPhrase())
                .suppressed(Arrays.asList(e.getSuppressed()))
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

}
