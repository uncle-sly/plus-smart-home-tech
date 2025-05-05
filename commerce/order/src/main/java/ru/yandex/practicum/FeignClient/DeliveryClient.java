package ru.yandex.practicum.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.controller.DeliveryOperations;

@FeignClient(name = "delivery")
public interface DeliveryClient extends DeliveryOperations {

}
