package ru.yandex.practicum.exception;

public class NoPaymentFoundException extends RuntimeException {
    public NoPaymentFoundException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
