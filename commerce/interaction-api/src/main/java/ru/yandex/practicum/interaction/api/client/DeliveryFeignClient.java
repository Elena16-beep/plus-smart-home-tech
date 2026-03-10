package ru.yandex.practicum.interaction.api.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {
    @PutMapping
    DeliveryDto createNewDelivery(@RequestBody @Valid DeliveryDto delivery);

    @PostMapping("/successful")
    void successfulDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void pickedInDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal costDelivery(@RequestBody @Valid OrderDto order);
}