package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseOperations {

    @PutMapping
    void addToWarehouse(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest);

    @PostMapping("/check")
    BookedProductsDto checkProductsQuantityInWarehouse(@RequestBody @Valid @NotNull ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductsInWarehouse(@RequestBody @Valid @NotNull AddProductToWarehouseRequest addProductToWarehouseRequest);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

}
