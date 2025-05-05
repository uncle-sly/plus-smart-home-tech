package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.FeignClient.DeliveryClient;
import ru.yandex.practicum.FeignClient.PaymentClient;
import ru.yandex.practicum.FeignClient.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {

        log.info("Получение заказов пользователя: {}", username);
        checkUser(username);

        return orderRepository.findAllByUsername(username)
                .stream()
                .map(orderMapper::mapToOrderDto)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("Создание нового заказа: {}", createNewOrderRequest);
        BookedProductsDto bookedProductsDto = warehouseClient.checkProductsQuantityInWarehouse(createNewOrderRequest.getShoppingCart());
        Order order = orderMapper.mapToOrder(createNewOrderRequest, bookedProductsDto);
        orderRepository.save(order);
        //Delivery
        AddressDto addressDto = warehouseClient.getWarehouseAddress();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .fromAddress(addressDto)
                .toAddress(createNewOrderRequest.getDeliveryAddress())
                .orderId(order.getOrderId())
                .deliveryState(DeliveryState.CREATED)
                .build();
        deliveryClient.planDelivery(deliveryDto);
        order.setDeliveryId(deliveryDto.getDeliveryId());
        return orderMapper.mapToOrderDto(order);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("Возврат товара по запросу: {}", productReturnRequest);
        Order order = CheckAndGetOrder(productReturnRequest.getOrderId());
        warehouseClient.acceptReturn(productReturnRequest.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Оплата заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Неудачная попытка оплаты заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Доставка заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Ошибка доставки заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчёт итоговой стоимости заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        BigDecimal totalPrice = paymentClient.totalCost(orderMapper.mapToOrderDto(order));
        order.setTotalPrice(totalPrice);
        PaymentDto payment = paymentClient.payment(orderMapper.mapToOrderDto(order));
        order.setPaymentId(payment.getPaymentId());
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчёт стоимости доставки заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        BigDecimal deliveryPrice = deliveryClient.deliveryCost(orderMapper.mapToOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        BigDecimal productPrice = paymentClient.productCost(orderMapper.mapToOrderDto(order));
        order.setProductPrice(productPrice);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);

        AssemblyProductsForOrderRequest assemblyProductsForOrderRequest = AssemblyProductsForOrderRequest.builder()
                .products(order.getProducts())
                .orderId(order.getOrderId())
                .build();
        warehouseClient.assemblyProductsForOrder(assemblyProductsForOrderRequest);

        order.setState(OrderState.ASSEMBLED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Ошибка сборки заказа: {}", orderId);
        Order order = CheckAndGetOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    private void checkUser(String userName) {
        if (null == userName || userName.isEmpty()) {
            throw new NotAuthorizedUserException(NotAuthorizedUserException.class, "Имя пользователя не должно быть пустым или состоять из пробелов");
        }
    }

    private Order CheckAndGetOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(NoOrderFoundException.class, "Заказ с id: " + orderId + "не найден."));
    }

}
