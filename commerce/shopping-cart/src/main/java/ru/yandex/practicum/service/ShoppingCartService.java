package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String userName);

    ShoppingCartDto addToShoppingCart(Map<UUID, Long> products, String userName);

    void deactivateShoppingCart(String userName);

    ShoppingCartDto removeProductFromShoppingCart(String userName, Set<UUID> products);

    ShoppingCartDto changeQuantityInShoppingCart(String userName, ChangeProductQuantityRequest changeProductQuantityRequest);

}
