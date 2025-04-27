package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookedProductsDto {

    @NotNull
    private double deliveryWeight;

    @NotNull
    private double deliveryVolume;

    private boolean fragile;

}
