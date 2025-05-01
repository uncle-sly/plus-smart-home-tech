package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

//    ShoppingCart mapToShoppingCart(ShoppingCartDto shoppingCartDto);

    ShoppingCartDto mapToShoppingCartDto(ShoppingCart shoppingCart);

}
