package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDto {

    @NotNull
    @Min(1)
    private double width;

    @NotNull
    @Min(1)
    private double height;

    @NotNull
    @Min(1)
    private double depth;

}
