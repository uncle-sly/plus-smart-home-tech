package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseOperations {

    @PutMapping
    void addToWarehouse(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest);

    @PostMapping("/check")
    BookedProductsDto checkProductsQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductsInWarehouse(@RequestBody @Valid @NotNull AddProductToWarehouseRequest addProductToWarehouseRequest);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    //передать товары в доставку
    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest shippedToDeliveryRequest);

    //принять возврат товаров на склад
    @PostMapping("/return")
    void acceptReturn(@RequestBody @NotNull Map<UUID, Long> returnProducts);

    //собрать товары к заказу
    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest assemblyProductsForOrderRequest);

}
