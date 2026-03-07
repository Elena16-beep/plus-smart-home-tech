package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.PaymentFeignClient;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeignClient {
    private final PaymentService paymentService;

    @Override
    public PaymentDto payment(OrderDto order) {
        log.info("Оплата для заказа: {}", order);

        return paymentService.payment(order);
    }

    @Override
    public OrderDto totalCost(OrderDto order) {
        log.info("Расчёт полной стоимости заказа: {}", order);

        return paymentService.totalCost(order);
    }

    @Override
    public void refund(UUID paymentId) {
        log.info("Метод для эмуляции успешной оплаты: {}", paymentId);

        paymentService.refund(paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("Расчёт стоимости товаров в заказе: {}", order);

        return paymentService.productCost(order);
    }

    @Override
    public void failed(UUID paymentId) {
        log.info("Метод для эмуляции отказа при оплате: {}", paymentId);
        paymentService.failed(paymentId);
    }
}