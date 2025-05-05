package ru.yandex.practicum.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.controller.PaymentOperations;

@FeignClient(name = "payment")
public interface PaymentClient extends PaymentOperations {

}
