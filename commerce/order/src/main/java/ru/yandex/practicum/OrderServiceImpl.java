package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.*;
import ru.yandex.practicum.interaction.api.dto.*;
import ru.yandex.practicum.interaction.api.enums.DeliveryState;
import ru.yandex.practicum.interaction.api.enums.OrderState;
import ru.yandex.practicum.interaction.api.exception.NoOrderFoundException;
import ru.yandex.practicum.interaction.api.request.*;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartFeignClient shoppingCartFeignClient;
    private final WarehouseFeignClient warehouseFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;
    private final PaymentFeignClient paymentFeignClient;

    @Override
    public Page<OrderDto> getOrdersByUser(String username, Pageable pageable) {
        ShoppingCartDto cart = shoppingCartFeignClient.getShoppingCart(username);

        return orderRepository.getAllOrdersByCartId(cart.getShoppingCartId(), pageable).map(orderMapper::mapToDto);
    }

    @Override
    @Transactional
    public OrderDto addOrder(CreateNewOrderRequest request) {
        log.info("Создание заказа: {}", request);

        BookedProductsDto bookedProducts =
                warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(request.getShoppingCart());

        Order order = buildNewOrder(request, bookedProducts);
        orderRepository.save(order);

        DeliveryDto delivery = planDelivery(order, request.getDeliveryAddress());
        order.setDeliveryId(delivery.getDeliveryId());

        PaymentDto payment = paymentFeignClient.payment(orderMapper.mapToDto(order));
        applyPayment(order, payment);

        orderRepository.save(order);
        log.info("Заказ создан: {}", order);

        return orderMapper.mapToDto(order);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = getOrderById(request.getOrderId());

        warehouseFeignClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        return changeState(orderId, OrderState.PAID);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        return changeState(orderId, OrderState.PAYMENT_FAILED);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        return changeState(orderId, OrderState.DELIVERED);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        return changeState(orderId, OrderState.DELIVERY_FAILED);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        return changeState(orderId, OrderState.ASSEMBLED);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        return changeState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        return changeState(orderId, OrderState.COMPLETED);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrderById(orderId);
        return paymentFeignClient.totalCost(orderMapper.mapToDto(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = getOrderById(orderId);

        BigDecimal deliveryPrice =
                deliveryFeignClient.cost(orderMapper.mapToDto(order));

        order.setDeliveryPrice(deliveryPrice);
        orderRepository.save(order);

        return orderMapper.mapToDto(order);
    }

    private Order buildNewOrder(CreateNewOrderRequest request, BookedProductsDto booked) {
        return Order.builder()
                .cartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .deliveryWeight(BigDecimal.valueOf(booked.getDeliveryWeight()))
                .deliveryVolume(BigDecimal.valueOf(booked.getDeliveryVolume()))
                .fragile(booked.getFragile())
                .build();
    }

    private void applyPayment(Order order, PaymentDto payment) {
        order.setPaymentId(payment.getPaymentId());
        order.setTotalPrice(payment.getTotalPayment());
        order.setDeliveryPrice(payment.getDeliveryTotal());
    }

    private DeliveryDto planDelivery(Order order, AddressDto deliveryAddress) {
        AddressDto warehouseAddress = warehouseFeignClient.getWarehouseAddress();

        AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(order.getProducts())
                .build();

        BookedProductsDto bookedProducts =
                warehouseFeignClient.assemblyProductsForOrder(request);

        DeliveryDto delivery = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(warehouseAddress)
                .toAddress(deliveryAddress)
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .fragile(bookedProducts.getFragile())
                .deliveryState(DeliveryState.CREATED)
                .build();

        return deliveryFeignClient.delivery(delivery);
    }

    private OrderDto changeState(UUID orderId, OrderState state) {
        Order order = getOrderById(orderId);
        order.setState(state);

        return orderMapper.mapToDto(orderRepository.save(order));
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ: " + orderId));
    }
}