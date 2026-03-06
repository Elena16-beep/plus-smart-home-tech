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
    DeliveryDto delivery(@RequestBody @Valid DeliveryDto delivery);

    @PostMapping("/successful")
    void successful(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void picked(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void failed(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal cost(@RequestBody @Valid OrderDto order);
}