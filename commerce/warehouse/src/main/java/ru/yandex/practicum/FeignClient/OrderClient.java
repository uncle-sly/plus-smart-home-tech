package ru.yandex.practicum.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.controller.OrderOperations;

@FeignClient(name = "order", contextId = "orderWarehouse")
public interface OrderClient extends OrderOperations {

}
