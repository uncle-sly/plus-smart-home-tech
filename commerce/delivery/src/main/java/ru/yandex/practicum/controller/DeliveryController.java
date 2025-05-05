package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("PUT /api/v1/delivery <--> Планирование доставки, создать новую доставку в БД: {}", deliveryDto);
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        log.info("POST /api/v1/delivery/successful <--> Эмуляция успешной доставки товара: orderId={}", orderId);
        deliveryService.deliverySuccessful(orderId);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        log.info("POST /api/v1/delivery/picked <--> Эмуляция получения товара в доставку: orderId={}", orderId);
        deliveryService.deliveryPicked(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        log.info("POST /api/v1/delivery/failed <--> Эмуляция неудачного вручения товара: orderId={}", orderId);
        deliveryService.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        log.info("POST /api/v1/delivery/cost <--> Расчёт полной стоимости доставки заказа: orderId={}", orderDto.getOrderId());
        return deliveryService.calculateDeliveryCost(orderDto);
    }

}
