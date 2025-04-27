package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private String country;

    private String city;

    private String street;

    private String house;

    private String flat;

}
