package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.DeliveryFeignClient;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeignClient {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto createNewDelivery(DeliveryDto delivery) {
        log.info("Создание новой доставки: {}", delivery);

        return deliveryService.createNewDelivery(delivery);
    }

    @Override
    public void successfulDelivery(UUID deliveryId) {
        log.info("Успешная доставка с id: {}", deliveryId);
        deliveryService.successfulDelivery(deliveryId);
    }

    @Override
    public void pickedInDelivery(UUID deliveryId) {
        log.info("Передача товара в доставку с id: {}", deliveryId);
        deliveryService.pickedInDelivery(deliveryId);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        log.info("Неудачная передача товара в доставку с id: {}", deliveryId);
        deliveryService.failedDelivery(deliveryId);
    }

    @Override
    public BigDecimal costDelivery(OrderDto order) {
        log.info("Расчёт полной стоимости доставки заказа: {}", order);

        return deliveryService.costDelivery(order);
    }
}