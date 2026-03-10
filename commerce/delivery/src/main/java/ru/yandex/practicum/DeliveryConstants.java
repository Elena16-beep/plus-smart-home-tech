package ru.yandex.practicum;

import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
public final class DeliveryConstants {
    public static final String WAREHOUSE_ADDRESS_1 = "г. Уфа, ул. Революционная, д. 98";
    public static final String WAREHOUSE_ADDRESS_2 = "г. Уфа, пр. Октября, д. 43";
    public static final String FIRST_WAREHOUSE = "ADDRESS_1";
    public static final String SECOND_WAREHOUSE = "ADDRESS_2";
    public static final String OTHER_ADDRESS = "OTHER_ADDRESS";
    public static final BigDecimal BASE_COST = new BigDecimal("5.0");
    public static final BigDecimal WAREHOUSE_ADDRESS_1_RATIO = new BigDecimal("1");
    public static final BigDecimal WAREHOUSE_ADDRESS_2_RATIO = new BigDecimal("2");
    public static final BigDecimal FRAGILE_RATIO = new BigDecimal("0.2");
    public static final BigDecimal WEIGHT_RATIO = new BigDecimal("0.3");
    public static final BigDecimal VOLUME_RATIO = new BigDecimal("0.1");
    public static final BigDecimal DELIVERY_ADDRESS_RATIO = new BigDecimal("0.2");
}