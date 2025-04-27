package ru.yandex.practicum.exception;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
