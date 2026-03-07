package ru.yandex.practicum;

import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto delivery(DeliveryDto delivery);

    void successful(UUID deliveryId);

    void picked(UUID deliveryId);

    void failed(UUID deliveryId);

    BigDecimal cost(OrderDto order);
}