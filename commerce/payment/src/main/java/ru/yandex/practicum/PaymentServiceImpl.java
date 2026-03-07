package ru.yandex.practicum;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.client.ShoppingStoreFeignClient;
import ru.yandex.practicum.interaction.api.dto.*;
import ru.yandex.practicum.interaction.api.enums.PaymentState;
import ru.yandex.practicum.interaction.api.exception.NoOrderFoundException;
import ru.yandex.practicum.interaction.api.exception.NotEnoughInfoInOrderToCalculateException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderFeignClient orderFeignClient;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        log.info("Формирование платежа для заказа: {}", order);
        checkOrder(order);

        OrderDto calculatedOrder = totalCost(order);
        BigDecimal vat = calculatedOrder.getProductPrice()
                .multiply(PaymentConstants.VAT_RATE);
        Payment payment = Payment.builder()
                .productsTotal(calculatedOrder.getProductPrice())
                .deliveryTotal(calculatedOrder.getDeliveryPrice())
                .feeTotal(vat)
                .totalPayment(calculatedOrder.getTotalPrice())
                .paymentState(PaymentState.PENDING)
                .orderId(calculatedOrder.getOrderId())
                .build();

        return paymentMapper.mapToDto(paymentRepository.save(payment));
    }

    @Override
    public OrderDto totalCost(OrderDto orderDto) {
        log.info("Расчёт полной стоимости заказа: {}", orderDto);
        checkOrder(orderDto);

        BigDecimal productCost = productCost(orderDto);
        BigDecimal deliveryCost =
                orderDto.getDeliveryPrice() != null ? orderDto.getDeliveryPrice() : calculateDeliveryPrice(orderDto);
        BigDecimal vat = productCost.multiply(PaymentConstants.VAT_RATE);
        BigDecimal total = productCost.add(deliveryCost).add(vat);

        orderDto.setProductPrice(productCost);
        orderDto.setDeliveryPrice(deliveryCost);
        orderDto.setTotalPrice(total);

        return orderDto;
//
//        @Override
//        public Double calculateTotalCostPayment(OrderDto orderDto) {
//            Float productPrice = orderDto.getProductPrice();
//
//            if (productPrice == null || orderDto.getDeliveryPrice() == null) {
//                throw new NotEnoughInfoInOrderToCalculateException(
//                        String.format("Стоимость заказа с ID = %s невозможно рассчитать. Одно из значений %f или %f = 0",
//                                orderDto.getOrderId(), productPrice, orderDto.getDeliveryPrice()));
//            }
//
//            return productPrice + productPrice * feeTax + orderDto.getDeliveryPrice();
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("Расчёт стоимости товаров: {}", order);

        Map<UUID, Long> products = order.getProducts();

        if (products == null || products.isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров");
        }

        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            ProductDto product;

            try {
                product = shoppingStoreFeignClient.getProduct(productId);
            } catch (FeignException.NotFound e) {
                throw new NotEnoughInfoInOrderToCalculateException(
                        "Товар с id %s не найден".formatted(productId)
                );
            }

            if (product.getPrice() == null) {
                throw new NotEnoughInfoInOrderToCalculateException(
                        "У товара %s не указана цена".formatted(productId)
                );
            }

            totalCost = totalCost.add(
                    product.getPrice().multiply(BigDecimal.valueOf(quantity))
            );
        }

        return totalCost;

//        Map<UUID, Long> products = order.getProducts();
//
//        Map<UUID, Float> price = products.keySet().stream()
//                .map(shoppingStoreFeignClient::getProduct)
//                .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));
//
//        return products.entrySet().stream()
//                .map(entry -> entry.getValue() * price.get(entry.getKey()))
//                .mapToDouble(Float::floatValue)
//                .sum();
    }

    @Override
    @Transactional
    public void refund(UUID paymentId) {
        log.info("Подтверждение успешной оплаты: {}", paymentId);

        Payment payment = paymentRepository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Платёж не найден"));

        payment.setPaymentState(PaymentState.SUCCESS);
        orderFeignClient.payment(payment.getOrderId());
    }

    @Override
    @Transactional
    public void failed(UUID paymentId) {
        log.info("Отказ при оплате: {}", paymentId);

        Payment payment = paymentRepository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Платёж не найден"));

        payment.setPaymentState(PaymentState.FAILED);
        orderFeignClient.paymentFailed(payment.getOrderId());
    }

    private BigDecimal calculateDeliveryPrice(OrderDto orderDto) {
        BigDecimal price = PaymentConstants.BASE_DELIVERY_PRICE;

        if (orderDto.getDeliveryWeight() != null &&
                orderDto.getDeliveryWeight()
                        .compareTo(PaymentConstants.HEAVY_WEIGHT_LIMIT) > 0) {
            price = price.add(PaymentConstants.HEAVY_DELIVERY_EXTRA);
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            price = price.add(PaymentConstants.FRAGILE_DELIVERY_EXTRA);
        }

        return price;
    }

    private void checkOrder(OrderDto orderDto) {
        if (orderDto == null || orderDto.getOrderId() == null ||
                orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных для расчёта заказа");
        }
    }
}