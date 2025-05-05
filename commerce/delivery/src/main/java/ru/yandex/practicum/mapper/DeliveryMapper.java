package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;


@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    Delivery mapToDelivery(DeliveryDto deliveryDto);

    DeliveryDto mapToDeliveryDto(Delivery delivery);

    @Mapping(target = "addressId", ignore = true)
    Address mapToAddress(AddressDto addressDto);

    AddressDto mapToAddressDto(Address address);

}
