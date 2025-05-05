package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@Builder
public class DeliveryDto {

    private UUID deliveryId;

    private AddressDto fromAddress;

    private AddressDto toAddress;

    private UUID orderId;

    private DeliveryState deliveryState;

}
