package ru.yandex.practicum.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
