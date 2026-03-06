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
    public DeliveryDto delivery(DeliveryDto delivery) {
        log.info("Создание новой доставки: {}", delivery);

        return deliveryService.delivery(delivery);
    }

    @Override
    public void successful(UUID deliveryId) {
        log.info("Эмуляция доставки с id: {}", deliveryId);
        deliveryService.successful(deliveryId);
    }

    @Override
    public void picked(UUID deliveryId) {
        log.info("Эмуляция передачи товара в доставку с id: {}", deliveryId);
        deliveryService.picked(deliveryId);
    }

    @Override
    public void failed(UUID deliveryId) {
        log.info("Эмуляция неудачной передачи товара  в доставку с id: {}", deliveryId);
        deliveryService.failed(deliveryId);
    }

    @Override
    public BigDecimal cost(OrderDto order) {
        log.info("Расчёт полной стоимости доставки заказа: {}", order);

        return deliveryService.cost(order);
    }
}