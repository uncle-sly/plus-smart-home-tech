package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService  shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {

        log.info("GET /api/v1/shopping-cart <--> Получить актуальную корзину для авторизованного пользователя: userName={}", userName);
        return shoppingCartService.getShoppingCart(userName);
    }

    @Override
    public ShoppingCartDto addToShoppingCart(Map<UUID, Long> products, String userName) {
        log.info("PUT /api/v1/shopping-cart <--> Добавить товар в корзину: userName={}, products={}", userName, products);
        return shoppingCartService.addToShoppingCart(products, userName);
    }

    @Override
    public void deactivateShoppingCart(String userName) {
        log.info("POST /api/v1/shopping-cart <--> Деактивация корзины товаров для пользователя: userName={}", userName);
        shoppingCartService.deactivateShoppingCart(userName);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String userName, Set<UUID> products) {
        log.info("POST /api/v1/shopping-cart/remove <--> Изменить состав товаров в корзине, т.е. удалить другие: userName={}, products={}", userName, products);
        return shoppingCartService.removeProductFromShoppingCart(userName, products);
    }

    @Override
    public ShoppingCartDto changeQuantityInShoppingCart(String userName, ChangeProductQuantityRequest changeProductQuantityRequest) {
        log.info("POST /api/v1/shopping-cart/change-quantity <--> Изменить количество товаров в корзине: userName={}, changeProductQuantityRequest={}", userName, changeProductQuantityRequest);
        return shoppingCartService.changeQuantityInShoppingCart(userName, changeProductQuantityRequest);
    }

}