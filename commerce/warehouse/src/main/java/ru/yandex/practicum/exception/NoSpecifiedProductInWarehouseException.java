package ru.yandex.practicum.exception;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
