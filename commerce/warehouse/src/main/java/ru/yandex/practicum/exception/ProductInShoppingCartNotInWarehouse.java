package ru.yandex.practicum.exception;

public class ProductInShoppingCartNotInWarehouse extends RuntimeException {
    public ProductInShoppingCartNotInWarehouse(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
