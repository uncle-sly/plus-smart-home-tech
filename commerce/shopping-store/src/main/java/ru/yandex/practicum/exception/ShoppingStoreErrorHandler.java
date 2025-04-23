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
public class ShoppingStoreErrorHandler {

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
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onProductNotFoundException(final ProductNotFoundException e) {
        log.error("ProductNotFoundException - 404: {}", e.getMessage(), e);
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//        String stackTrace = sw.toString();
        return getErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAnyException(final RuntimeException e) {
        log.error("Error:500; {}", e.getMessage(), e);
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//        String stackTrace = sw.toString();
        return getErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

