package ru.yandex.practicum.exception;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
