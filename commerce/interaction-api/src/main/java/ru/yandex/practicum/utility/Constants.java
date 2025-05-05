package ru.yandex.practicum.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final BigDecimal TAX = BigDecimal.valueOf(0.1);

    public static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};

    public static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    public static final BigDecimal BASE_RATE = BigDecimal.valueOf(5);
    public static final BigDecimal WAREHOUSE_1_ADDRESS_MULTIPLIER = BigDecimal.valueOf(1.0);
    public static final BigDecimal WAREHOUSE_2_ADDRESS_MULTIPLIER = BigDecimal.valueOf(2.0);
    public static final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(0.2);
    public static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(0.3);
    public static final BigDecimal VOLUME_MULTIPLIER = BigDecimal.valueOf(0.2);
    public static final BigDecimal STREET_MULTIPLIER = BigDecimal.valueOf(0.2);

}
