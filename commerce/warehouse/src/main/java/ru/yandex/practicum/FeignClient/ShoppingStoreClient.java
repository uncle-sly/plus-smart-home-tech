package ru.yandex.practicum.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.controller.ShoppingStoreOperations;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {

}
