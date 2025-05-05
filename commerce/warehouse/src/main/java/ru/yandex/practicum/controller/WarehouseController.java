package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    @Override
    public void addToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        log.info("PUT /api/v1/warehouse <--> Добавить товар на Склад: {}", newProductInWarehouseRequest);
        warehouseService.addToWarehouse(newProductInWarehouseRequest);
    }

    @Override
    public BookedProductsDto checkProductsQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        log.info("POST /api/v1/warehouse/check <--> Предварительно проверить, что количество товаров на складе достаточно для данной корзиный продуктов: {}", shoppingCartDto);

        return warehouseService.checkProductsQuantityInWarehouse(shoppingCartDto);
    }

    @Override
    public void addProductsInWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        log.info("POST /api/v1/warehouse/add <--> Принять товар на склад: {}", addProductToWarehouseRequest);
        warehouseService.addProductsInWarehouse(addProductToWarehouseRequest);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("GET /api/v1/warehouse/address <--> Актуальный адрес склада.");

        return warehouseService.getWarehouseAddress();
    }

//    новые endpoint-ы
    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("POST /api/v1/warehouse/shipped <--> Передача товаров в доставку: {}", request);
        warehouseService.shippedToDelivery(request);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> returnProducts) {
        log.info("POST /api/v1/warehouse/return <--> Принятие возврата товаров на склад: {}", returnProducts);
        warehouseService.acceptReturn(returnProducts);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest) {
        log.info("POST /api/v1/warehouse/assembly <--> Сборка товаров к заказу для подготовки к отправке: {}", assemblyProductsForOrderRequest);
        return warehouseService.assemblyProductsForOrder(assemblyProductsForOrderRequest);
    }

}