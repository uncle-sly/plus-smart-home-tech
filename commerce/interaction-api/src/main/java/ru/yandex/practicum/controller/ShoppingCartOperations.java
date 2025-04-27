package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartOperations {


    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam @NotNull String userName);

    @PutMapping
    ShoppingCartDto addToShoppingCart(@RequestBody @Valid Map<UUID, Long> products, @RequestParam @NotNull String userName);

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam @NotNull String userName);

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromShoppingCart(@RequestParam @NotNull String userName, @RequestBody @Valid @NotNull Set<UUID> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantityInShoppingCart(@RequestParam @NotNull String userName, @RequestBody @Valid ChangeProductQuantityRequest changeProductQuantityRequest);

}
