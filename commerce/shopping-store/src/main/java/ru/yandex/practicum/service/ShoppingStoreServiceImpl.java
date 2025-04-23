package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.ProductState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShoppingStoreServiceImpl implements ShoppingStoreService {


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductList(ProductCategory category, Pageable pageable) {
        log.info("getProductList: category={}, pageable={}", category, pageable);
        return productRepository.findAllByProductCategory(category,pageable)
                .stream().map(productMapper::mapToProductDto)
                .toList();
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("createProduct: productDto={}", productDto);
        return productMapper.mapToProductDto(productRepository.save(productMapper.mapToProduct(productDto)));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("updateProduct: productDto={}", productDto);
        return productMapper.mapToProductDto(productRepository.save(productMapper.mapToProduct(productDto)));
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        log.info("removeProductFromStore: productId={}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(Product.class, " c ID - " + productId + ", не найден."));
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("setProductQuantityState: setProductQuantityStateRequest={}", setProductQuantityStateRequest);
        Product product = productRepository.findById(setProductQuantityStateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(Product.class, " c ID - " + setProductQuantityStateRequest.getProductId() + ", не найден."));

        product.setQuantityState(setProductQuantityStateRequest.getQuantityState());
        productRepository.save(product);
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("getProduct: productId={}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(Product.class, " c ID - " + productId + ", не найден."));
        return productMapper.mapToProductDto(product);
    }

}
