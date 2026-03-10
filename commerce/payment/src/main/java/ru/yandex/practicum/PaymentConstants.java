package ru.yandex.practicum;

import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
public final class PaymentConstants {
    public static final BigDecimal VAT_RATE = new BigDecimal("0.10");
    public static final BigDecimal BASE_DELIVERY_PRICE = BigDecimal.valueOf(500);
    public static final BigDecimal HEAVY_DELIVERY_EXTRA = BigDecimal.valueOf(200);
    public static final BigDecimal FRAGILE_DELIVERY_EXTRA = BigDecimal.valueOf(300);
    public static final BigDecimal HEAVY_WEIGHT_LIMIT = BigDecimal.valueOf(5);
}