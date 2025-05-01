package ru.yandex.practicum.dto.shoppingCart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {

    @NotNull
    private UUID shoppingCartId;

    @NotNull
    private Map<UUID, Long> products;

}
