package ru.yandex.practicum.exception;

public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException(Class<?> entityClass, String message) {
        super(entityClass.getSimpleName() + message);
    }
}
