package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.FeignClient.OrderClient;
import ru.yandex.practicum.FeignClient.ShoppingStoreClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.PaymentState;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.*;

import static ru.yandex.practicum.utility.Constants.TAX;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;


    @Override
    public PaymentDto payment(OrderDto orderDto) {
        checkPrices(orderDto);
        return paymentMapper.mapToPaymentDto(paymentRepository.save(paymentMapper.mapToPayment(orderDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        checkPrices(orderDto);

        List<BigDecimal> costs = new ArrayList<>();
        Map<UUID, Long> products = orderDto.getProducts();
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDto productDto = shoppingStoreClient.getProduct(entry.getKey());
            BigDecimal totalCost = productDto.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
            costs.add(totalCost);
        }
        return costs.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        checkPrices(orderDto);
        BigDecimal productCost = orderDto.getProductPrice();
        BigDecimal deliveryCost = orderDto.getDeliveryPrice();
        return deliveryCost.add(productCost).add(productCost.multiply(TAX));
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("успешная оплата в платежном шлюзе с paymentId={}", paymentId);
        Payment payment = checkPayment(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
        paymentRepository.save(payment);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("НЕ успешная оплата в платежном шлюзе с paymentId={}", paymentId);
        Payment payment = checkPayment(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        paymentRepository.save(payment);
    }


    private Payment checkPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException(NoPaymentFoundException.class, "Платеж с id: " + paymentId + "не найден."));
    }

    private void checkPrices(OrderDto orderDto) {
        List<BigDecimal> prices = List.of(orderDto.getTotalPrice(), orderDto.getDeliveryPrice(), orderDto.getProductPrice());

        for (BigDecimal price : prices) {
            if (null == price || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new NotEnoughInfoInOrderToCalculateException(NotEnoughInfoInOrderToCalculateException.class,
                        "Недостаточно информации в заказе для формирования оплаты");
            }
        }
    }

}