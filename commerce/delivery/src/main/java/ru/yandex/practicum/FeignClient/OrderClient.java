package ru.yandex.practicum.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.controller.OrderOperations;

@FeignClient(name = "order")
public interface OrderClient extends OrderOperations {

}
