package ru.yandex.practicum.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
