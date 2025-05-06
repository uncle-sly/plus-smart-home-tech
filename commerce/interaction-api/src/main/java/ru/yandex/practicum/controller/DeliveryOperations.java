package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void deliverySuccessful(@RequestParam @NotNull UUID orderId);

    @PostMapping("/picked")
    void deliveryPicked(@RequestParam @NotNull UUID orderId);

    @PostMapping("/failed")
    void deliveryFailed(@RequestParam @NotNull UUID orderId);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody @Valid OrderDto orderDto);

}
