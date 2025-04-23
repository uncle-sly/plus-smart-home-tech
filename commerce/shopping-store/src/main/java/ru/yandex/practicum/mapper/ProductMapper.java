package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product mapToProduct(ProductDto productDto);

    ProductDto mapToProductDto(Product product);

}
