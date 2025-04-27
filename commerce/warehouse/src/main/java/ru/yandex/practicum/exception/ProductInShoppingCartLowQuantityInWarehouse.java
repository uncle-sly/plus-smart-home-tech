package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouse(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
