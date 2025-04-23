package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ShoppingCartStatusException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {


    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String userName) {
        log.info("Get shopping cart for user {}", userName);
        checkUser(userName);
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCartRepository.findByUserName(userName)
                .orElseGet(() -> (shoppingCartRepository.save(createNewShoppingCart(userName)))));
    }

    @Override
    public ShoppingCartDto addToShoppingCart(Map<UUID, Long> products, String userName) {
        log.info("Add to shopping cart for user {} with products {}", userName, products);
        checkUser(userName);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(userName)
                .orElseGet(() -> (shoppingCartRepository.save(createNewShoppingCart(userName))));

        checkCartStatus(shoppingCart);

        Map<UUID, Long> newProducts = shoppingCart.getProducts();
        newProducts.putAll(products);

        shoppingCart.setProducts(newProducts);
        // проверка на складе

        return shoppingCartMapper.mapToShoppingCartDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public void deactivateShoppingCart(String userName) {
        log.info("Deactivate shopping cart for user {}", userName);
        checkUser(userName);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(userName)
                .orElseGet(() -> (shoppingCartRepository.save(createNewShoppingCart(userName))));
        checkCartStatus(shoppingCart);
        shoppingCart.setActive(false);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String userName, Set<UUID> products) {
        log.info("removeProductFromShoppingCart: {} with products {}", userName, products);

        return null;
    }

    @Override
    public ShoppingCartDto changeQuantityInShoppingCart(String userName, SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("setProductQuantityState: setProductQuantityStateRequest={}", setProductQuantityStateRequest);

        return null;
    }

    private void checkUser(String userName) {
        if (null == userName || userName.isEmpty()) {
            throw new NotAuthorizedUserException(NotAuthorizedUserException.class, "Имя пользователя не должно быть пустым или состоять из пробелов");
        }
    }

    private ShoppingCart createNewShoppingCart(String userName) {
        return ShoppingCart.builder()
                .userName(userName)
                .products(new HashMap<>())
                .active(true)
                .build();
    }

    private void checkCartStatus(ShoppingCart shoppingCart) {
        if (!shoppingCart.getActive()) {
            throw new ShoppingCartStatusException(ShoppingCartStatusException.class, "Корзина пользователя "+ shoppingCart.getUserName() + " деактивирована.");
        }
    }
}
