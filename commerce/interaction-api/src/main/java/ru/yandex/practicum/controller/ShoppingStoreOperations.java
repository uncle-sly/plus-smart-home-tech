package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

//@RequestMapping("/api/v1/shopping-store")
public interface ShoppingStoreOperations {


    @GetMapping
    List<ProductDto> getProductList(@RequestParam @NotNull ProductCategory category, Pageable pageable);

    @PutMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestParam @NotNull UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityStateRequest setProductQuantityStateRequest);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable @NotNull UUID productId);

}
