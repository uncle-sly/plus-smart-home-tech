package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.exception.ErrorResponseBuild.getErrorResponse;

@Slf4j
@RestControllerAdvice
public class ShoppingCartErrorHandler {

    //  перехват эксепшенов валидации
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e
    ) {
        final List<ValidationViolation> validationViolations = e.getConstraintViolations().stream()
                .map(
                        violation -> {
                            log.error("ConstraintViolationException: {} : {}", violation.getPropertyPath().toString(), violation.getMessage());
                            return new ValidationViolation(
                                    violation.getPropertyPath().toString(),
                                    violation.getMessage()
                            );
                        }
                )
                .collect(Collectors.toList());

        return new ValidationErrorResponse(validationViolations);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<ValidationViolation> validationViolations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                            log.error("MethodArgumentNotValidException: {} : {}", error.getField(), error.getDefaultMessage());
                            return new ValidationViolation(error.getField(), error.getDefaultMessage());
                        }
                )
                .collect(Collectors.toList());

        return new ValidationErrorResponse(validationViolations);
    }


    //  перехват эксепшенов
    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNoProductsInShoppingCartException(final NoProductsInShoppingCartException e) {
        log.error("NoProductsInShoppingCartException - 404: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse onNotAuthorizedUserException(final NotAuthorizedUserException e) {
        log.error("NotAuthorizedUserException - 401: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ShoppingCartStatusException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse onShoppingCartStatusException(final ShoppingCartStatusException e) {
        log.error("ShoppingCartStatusException - 403: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAnyException(final RuntimeException e) {
        log.error("Error:500; {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

