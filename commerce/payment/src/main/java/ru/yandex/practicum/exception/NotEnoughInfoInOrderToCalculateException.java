package ru.yandex.practicum.exception;

public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    public NotEnoughInfoInOrderToCalculateException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
