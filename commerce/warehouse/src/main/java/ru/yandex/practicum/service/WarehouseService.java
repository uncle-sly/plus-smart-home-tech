package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;


public interface WarehouseService {

    void addToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkProductsQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void addProductsInWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest);

    AddressDto getWarehouseAddress();

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> returnProducts);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest);

}
