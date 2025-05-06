package ru.yandex.practicum.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/order")
public class OrderController implements OrderOperations {

    private final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("GET /api/v1/order <--> Получение заказов клиента: username={}", username);
        return orderService.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("PUT /api/v1/order <--> Создание нового заказа: {}", createNewOrderRequest);
        return orderService.createNewOrder(createNewOrderRequest);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("POST /api/v1/order/return <--> Возврат товара: {}", productReturnRequest);
        return orderService.productReturn(productReturnRequest);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("POST /api/v1/order/payment <--> Оплата заказа: orderId={}", orderId);
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("POST /api/v1/order/payment/failed <--> Ошибка оплаты: orderId={}", orderId);
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("POST /api/v1/order/delivery <--> Заказ передан в доставку: orderId={}", orderId);
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("POST /api/v1/order/delivery/failed <--> Ошибка доставки: orderId={}", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("POST /api/v1/order/completed <--> Завершение заказа: orderId={}", orderId);
        return orderService.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("POST /api/v1/order/calculate/total <--> Расчёт общей стоимости: orderId={}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("POST /api/v1/order/calculate/delivery <--> Расчёт стоимости доставки: orderId={}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("POST /api/v1/order/assembly <--> Сборка заказа: orderId={}", orderId);
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("POST /api/v1/order/assembly/failed <--> Ошибка сборки заказа: orderId={}", orderId);
        return orderService.assemblyFailed(orderId);
    }

}
