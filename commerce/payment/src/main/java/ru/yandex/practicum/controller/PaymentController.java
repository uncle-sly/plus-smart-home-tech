package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        log.info("POST /api/v1/payment <--> Формирования оплаты заказа: {}", orderDto);
        return paymentService.payment(orderDto);
    }

    @Override
    public BigDecimal totalCost(OrderDto orderDto) {
        log.info("POST /api/v1/payment/totalCost <--> Расчёт полной стоимости заказа для: {}", orderDto);
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        log.info("POST /api/v1/payment/productCost <--> Расчёт полной стоимости товаров в заказе: {}", orderDto);
        return paymentService.calculateProductCost(orderDto);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("POST /api/v1/payment/refund <--> Эмуляция успешной оплаты. PaymentId: {}", paymentId);
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("POST /api/v1/payment/failed <--> Эмуляция отказа оплаты. PaymentId: {}", paymentId);
        paymentService.paymentFailed(paymentId);
    }

}
