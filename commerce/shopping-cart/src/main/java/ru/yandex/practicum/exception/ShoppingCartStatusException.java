package ru.yandex.practicum.exception;

public class ShoppingCartStatusException extends RuntimeException {
    public ShoppingCartStatusException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
