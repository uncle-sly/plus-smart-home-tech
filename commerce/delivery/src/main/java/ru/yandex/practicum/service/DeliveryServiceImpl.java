package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.FeignClient.OrderClient;
import ru.yandex.practicum.FeignClient.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static ru.yandex.practicum.utility.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.debug("Планируем доставку: {}", deliveryDto);
        return deliveryMapper.mapToDeliveryDto(deliveryRepository.save(deliveryMapper.mapToDelivery(deliveryDto)));
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        log.debug("Доставка успешна для orderId={}", orderId);
        Delivery delivery = getDelivery(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.delivery(orderId);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        log.debug("Товар передан в доставку для orderId={}", orderId);
        Delivery delivery = getDelivery(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.delivery(delivery.getOrderId());
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(delivery.getOrderId(), delivery.getDeliveryId()));
        deliveryRepository.save(delivery);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        log.debug("Доставка не удалась для orderId={}", orderId);
        Delivery delivery = getDelivery(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryFailed(delivery.getOrderId());
        deliveryRepository.save(delivery);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        log.debug("Вычисление стоимости доставки для заказа: {}", orderDto.getOrderId());
        Delivery delivery = getDelivery(orderDto.getOrderId());

        Address warehouseAddress = delivery.getFromAddress();
        Address clientAddress = delivery.getToAddress();

        BigDecimal warehouseMultiplier = warehouseAddress.getCity().equals("ADDRESS_1")
                ? WAREHOUSE_1_ADDRESS_MULTIPLIER
                : WAREHOUSE_2_ADDRESS_MULTIPLIER;

        BigDecimal base = BASE_RATE.multiply(BigDecimal.ONE.add(warehouseMultiplier));

        BigDecimal fragileFee = Boolean.TRUE.equals(orderDto.getFragile())
                ? base.multiply(FRAGILE_MULTIPLIER)
                : BigDecimal.ZERO;

        BigDecimal weightCost = BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(WEIGHT_MULTIPLIER);
        BigDecimal volumeCost = BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(VOLUME_MULTIPLIER);

        //до учета улицы
        BigDecimal subtotal = base.add(fragileFee).add(weightCost).add(volumeCost);

        BigDecimal streetFee = !warehouseAddress.getStreet().equals(clientAddress.getStreet())
                ? base.multiply(STREET_MULTIPLIER)
                : BigDecimal.ZERO;

        BigDecimal totalCost = subtotal.add(streetFee);

        log.debug("Стоимость доставки: {}", totalCost);
        return totalCost;
    }

    private Delivery getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(NoDeliveryFoundException.class, "Доставка с id:" + deliveryId + " не найдена."));
    }

}
