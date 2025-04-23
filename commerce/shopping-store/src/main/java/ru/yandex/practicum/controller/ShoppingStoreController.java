package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ShoppingStoreService shoppingStoreService;

    @Override
//    @GetMapping
    public List<ProductDto> getProductList(ProductCategory category, Pageable pageable) {

        log.info("GET /api/v1/shopping-store <--> Получение списка товаров по типу в пагинированном виде: " +
                "category {}, pageable {} ", category, pageable);
        return shoppingStoreService.getProductList(category, pageable);
    }

    @Override
//    @PutMapping
    public ProductDto createProduct(ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store <--> Создание нового товара в ассортименте:  productDto {}", productDto);
        return shoppingStoreService.createProduct(productDto);
    }

    @Override
//    @PostMapping
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("POST /api/v1/shopping-store <--> Обновление товара в ассортименте: productDto {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
//    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore <--> Удалить товар из ассортимента магазина.(Функция менеджеров): productId {}", productId);
        return shoppingStoreService.removeProductFromStore(productId);
    }

    // API вызывается со стороны склада
    @Override
//    @PostMapping("/quantityState")
    public boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("POST /api/v1/shopping-store/quantityState <--> Установка статуса по товару: setProductQuantityStateRequest {}", setProductQuantityStateRequest);
        return shoppingStoreService.setProductQuantityState(setProductQuantityStateRequest);
    }

    @Override
//    @GetMapping("/{productId}")
    public ProductDto getProduct(UUID productId) {
        log.info("GET /api/v1/shopping-store/{productId} <--> Получить сведения по товару из БД: productId {} ", productId);
        return shoppingStoreService.getProduct(productId);
    }

}