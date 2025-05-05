package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.FeignClient.OrderClient;
import ru.yandex.practicum.FeignClient.ShoppingStoreClient;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.utility.Constants.CURRENT_ADDRESS;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final BookingRepository bookingRepository;
    private final WarehouseMapper warehouseMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;


    @Override
    public void addToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        log.info("Add product to warehouse");
        checkWarehouseProduct(newProductInWarehouseRequest.getProductId());
        warehouseRepository.save(warehouseMapper.mapToWarehouseProduct(newProductInWarehouseRequest));
    }

    @Override
    public BookedProductsDto checkProductsQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {

        log.info("Получаем товары из shoppingCartDto {}", shoppingCartDto);
        Map<UUID, Long> shoppingCartProducts = shoppingCartDto.getProducts();
        log.info("Получаем количество товаров на складе {}", shoppingCartProducts.keySet());
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(shoppingCartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        shoppingCartProducts.entrySet().forEach(product -> calculateProducts(
                product,
                warehouseProducts,
                bookedProductsDto));
        return bookedProductsDto;
    }

    @Override
    public void addProductsInWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        log.info("Принимаем товар на склад: {}", addProductToWarehouseRequest);
        WarehouseProduct warehouseProduct = getWarehouseProduct(addProductToWarehouseRequest.getProductId());
        Long newQuantity = warehouseProduct.getQuantity() + addProductToWarehouseRequest.getQuantity();
        warehouseProduct.setQuantity(newQuantity);
        warehouseRepository.save(warehouseProduct);

        log.info("Обновляем данные в shopping-store");
        shoppingStoreClient.setProductQuantityState(new SetProductQuantityStateRequest(warehouseProduct.getProductId(), getQuantityState(newQuantity)));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getWarehouseAddress() {
        return new AddressDto(CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest) {
        log.info("Передача товаров в доставку: {}", shippedToDeliveryRequest);
        Booking booking = checkAndGetWarehouseBooking(shippedToDeliveryRequest.getOrderId());
        booking.setDeliveryId(shippedToDeliveryRequest.getDeliveryId());
        bookingRepository.save(booking);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> returnProducts) {
        log.info("Возврат товаров на склад: {}", returnProducts);
        List<AddProductToWarehouseRequest> addProductToWarehouseRequests = returnProducts.entrySet().stream()
                .map(entry -> new AddProductToWarehouseRequest(entry.getKey(), entry.getValue()))
                .toList();
        addProductToWarehouseRequests.forEach(this::addProductsInWarehouse);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest) {
        log.info("Сборка товаров для заказа: {}", assemblyProductsForOrderRequest);
        Map<UUID, Long> assemblingProducts = assemblyProductsForOrderRequest.getProducts();
        log.info("Получаем количество товаров на складе {}", assemblingProducts.keySet());
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(assemblingProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        try {
            assemblingProducts.entrySet().forEach(product -> calculateProducts(
                    product,
                    warehouseProducts,
                    bookedProductsDto));
        } catch (ProductInShoppingCartLowQuantityInWarehouse e) {
            orderClient.assemblyFailed(assemblyProductsForOrderRequest.getOrderId());
        }
        //уменьшаем остатки на складе
        calculateWarehouseBookings(assemblingProducts);
        Booking newBooking = warehouseMapper.mapToBooking(assemblyProductsForOrderRequest);
        bookingRepository.save(newBooking);
        return bookedProductsDto;
    }


    private Booking checkAndGetWarehouseBooking(UUID orderId) {
        return bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException(NoOrderFoundException.class, "Заказ с id=" + orderId + " не найден на Складе."));
    }

    private void calculateWarehouseBookings(Map<UUID, Long> products) {
        products.forEach((id, value) -> {
            WarehouseProduct warehouseProduct = getWarehouseProduct(id);
            Long quantity = warehouseProduct.getQuantity();
            warehouseProduct.setQuantity(quantity - value);
            warehouseRepository.save(warehouseProduct);
            shoppingStoreClient.setProductQuantityState(
                    new SetProductQuantityStateRequest(warehouseProduct.getProductId(), getQuantityState(warehouseProduct.getQuantity())));
        });
    }

    private void calculateProducts(Map.Entry<UUID, Long> shoppingCartProduct,
                                   Map<UUID, WarehouseProduct> warehouseProducts,
                                   BookedProductsDto bookedProductsDto) {

        WarehouseProduct warehouseProduct = warehouseProducts.get(shoppingCartProduct.getKey());
        if (warehouseProduct == null) {
            throw new NoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException.class, "Продукта с UUID=" + shoppingCartProduct.getKey() + " нет на Складе.");
        }
        if (warehouseProduct.getQuantity() < shoppingCartProduct.getValue()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse.class, "Продукта с UUID=" + shoppingCartProduct.getKey() + " осталось мало на Складе.");
        } else {
            calculateDelivery(shoppingCartProduct, warehouseProduct, bookedProductsDto);
        }
    }

    private void calculateDelivery(Map.Entry<UUID, Long> shoppingCartProduct,
                                   WarehouseProduct warehouseProduct,
                                   BookedProductsDto bookedProductsDto) {

        double weight = bookedProductsDto.getDeliveryWeight() + warehouseProduct.getWeight() * shoppingCartProduct.getValue();
        bookedProductsDto.setDeliveryWeight(weight);
        double volume = bookedProductsDto.getDeliveryVolume() +
                (warehouseProduct.getDepth() * warehouseProduct.getHeight() * warehouseProduct.getWidth()) * shoppingCartProduct.getValue();
        bookedProductsDto.setDeliveryVolume(volume);
        if (warehouseProduct.getFragile()) {
            bookedProductsDto.setFragile(true);
        }
    }

    private void checkWarehouseProduct(UUID productId) {
        if (warehouseRepository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(WarehouseProduct.class, "Продукт с id=" + productId + " уже есть на Складе.");
        }
    }

    private WarehouseProduct getWarehouseProduct(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException.class, "Продукта с UUID=" + productId + " нет на Складе."));
    }

    private QuantityState getQuantityState(Long quantity) {
        QuantityState quantityState;

        if (quantity > 100) {
            quantityState = QuantityState.MANY;
        } else if (quantity > 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (quantity > 0) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.ENDED;
        }
        return quantityState;
    }

}
