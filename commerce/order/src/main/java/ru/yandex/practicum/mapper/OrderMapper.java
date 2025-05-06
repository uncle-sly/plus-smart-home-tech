package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.Order;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto mapToOrderDto(Order order);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "products", source = "createNewOrderRequest.shoppingCart.products")
    @Mapping(target = "shoppingCartId", source = "createNewOrderRequest.shoppingCart.shoppingCartId")
    @Mapping(target = "deliveryWeight", source = "bookedProductsDto.deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "bookedProductsDto.deliveryVolume")
    @Mapping(target = "fragile", source = "bookedProductsDto.fragile")
    @Mapping(target = "state", constant = "NEW")
    Order mapToOrder(CreateNewOrderRequest createNewOrderRequest, BookedProductsDto bookedProductsDto);

}
