package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.model.Payment;


@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "totalPayment", ignore = true)
    @Mapping(target = "deliveryTotal", ignore = true)
    @Mapping(target = "feeTotal", ignore = true)
    @Mapping(target = "paymentState", ignore = true)
    Payment mapToPayment(OrderDto orderDto);

    PaymentDto mapToPaymentDto(Payment payment);

}
