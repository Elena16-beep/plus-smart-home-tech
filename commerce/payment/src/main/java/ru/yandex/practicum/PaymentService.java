package ru.yandex.practicum;

import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto order);

    OrderDto totalCost(OrderDto order);

    void refund(UUID paymentId);

    BigDecimal productCost(OrderDto order);

    void failed(UUID paymentId);
}