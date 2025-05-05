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
public class WarehouseErrorHandler {

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
    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onNoSpecifiedProductInWarehouseException(final NoSpecifiedProductInWarehouseException e) {
        log.error("NoSpecifiedProductInWarehouseException - 400: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onProductInShoppingCartLowQuantityInWarehouse(final ProductInShoppingCartLowQuantityInWarehouse e) {
        log.error("ProductInShoppingCartLowQuantityInWarehouse - 400: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductInShoppingCartNotInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onProductInShoppingCartNotInWarehouse(final ProductInShoppingCartNotInWarehouse e) {
        log.error("ProductInShoppingCartNotInWarehouse - 400: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onSpecifiedProductAlreadyInWarehouseException(final SpecifiedProductAlreadyInWarehouseException e) {
        log.error("SpecifiedProductAlreadyInWarehouseException - 400: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNoOrderFoundException(final NoOrderFoundException e) {
        log.error("NoOrderFoundException - 404: {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAnyException(final RuntimeException e) {
        log.error("Error:500; {}", e.getMessage(), e);
        return getErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

