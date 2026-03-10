package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.request.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.request.ProductReturnRequest;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderFeignClient {
    private final OrderService orderService;

    @Override
    public Page<OrderDto> getOrdersByUser(String username, Pageable pageable) {
        log.info("Получение заказов пользователя: {}", username);

        return orderService.getOrdersByUser(username, pageable);
    }

    @Override
    public OrderDto addOrder(CreateNewOrderRequest request) {
        log.info("Создание заказа: {}", request);

        return orderService.addOrder(request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Запрос на возврат заказа: {}", request);

        return orderService.productReturn(request);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Оплата заказа с id: {}", orderId);

        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Оплата заказа  с id {} не прошла", orderId);

        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Доставка заказа с id: {}", orderId);

        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Заказ с id {} не доставлен", orderId);

        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Завершение заказа с id: {}", orderId);

        return orderService.complete(orderId);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        log.info("Расчёт стоимости заказа с id: {}", orderId);

        return orderService.calculateTotal(orderId);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("Расчёт стоимости доставки заказа с id: {}", orderId);

        return orderService.calculateDelivery(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Сборка заказа с id: {}", orderId);

        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Ошибка сборки заказа с id {}", orderId);

        return orderService.assemblyFailed(orderId);
    }
}