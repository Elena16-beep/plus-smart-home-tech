package ru.yandex.practicum;

import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createNewDelivery(DeliveryDto delivery);

    void successfulDelivery(UUID deliveryId);

    void pickedInDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    BigDecimal costDelivery(OrderDto order);
}