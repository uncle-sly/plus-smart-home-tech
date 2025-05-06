package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(source = "dimension.width", target = "width")
    @Mapping(source = "dimension.height", target = "height")
    @Mapping(source = "dimension.depth", target = "depth")
    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct mapToWarehouseProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    Booking mapToBooking(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest);

}
