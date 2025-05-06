package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping()
    PaymentDto payment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal totalCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/refund")
    void paymentSuccess(@RequestParam @NotNull UUID paymentId);

    @PostMapping("/failed")
    void paymentFailed(@RequestParam @NotNull UUID paymentId);

}
