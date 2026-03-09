package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.enums.DeliveryState;
import ru.yandex.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.yandex.practicum.interaction.api.request.ShippedToDeliveryRequest;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderFeignClient orderFeignClient;
    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    @Transactional
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        log.info("Создание доставки: {}", deliveryDto);

        Delivery delivery = deliveryMapper.mapToEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        Delivery saved = deliveryRepository.save(delivery);

        log.info("Создана доставка с id: {}", saved.getDeliveryId());

        return deliveryMapper.mapToDto(saved);
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        log.info("Успешная доставка: {}", deliveryId);

        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);

        orderFeignClient.delivery(delivery.getOrderId());
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void pickedInDelivery(UUID deliveryId) {
        log.info("Передача товара в доставку: {}", deliveryId);

        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderFeignClient.assembly(delivery.getOrderId());

        warehouseFeignClient.shippedToDelivery(
                new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId)
        );

        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void failedDelivery(UUID deliveryId) {
        log.info("Доставка не удалась: {}", deliveryId);

        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);

        OrderDto orderDto = orderFeignClient.deliveryFailed(delivery.getOrderId());
        log.info("Заказ не доставлен, orderDto: {}", orderDto);

        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public BigDecimal costDelivery(OrderDto orderDto) {
        log.info("Расчёт стоимости доставки для заказа: {}", orderDto.getOrderId());

        Delivery delivery = getDeliveryById(orderDto.getDeliveryId());

        delivery.setDeliveryWeight(orderDto.getDeliveryWeight().doubleValue());
        delivery.setDeliveryVolume(orderDto.getDeliveryVolume().doubleValue());
        delivery.setFragile(orderDto.getFragile());

        BigDecimal totalCost = calculateTotalCost(delivery);

        deliveryRepository.save(delivery);
        log.info("Стоимость доставки: {}", totalCost);

        return totalCost;
    }

    private BigDecimal calculateTotalCost(Delivery delivery) {
        String fromStreet = delivery.getFromAddress().getStreet();
        String toStreet = delivery.getToAddress().getStreet();
        BigDecimal totalCost = DeliveryConstants.BASE_COST;

        if (DeliveryConstants.WAREHOUSE_ADDRESS_1.equals(fromStreet)) {
            totalCost = totalCost.add(
                    DeliveryConstants.BASE_COST.multiply(DeliveryConstants.WAREHOUSE_ADDRESS_1_RATIO)
            );
        } else if (DeliveryConstants.WAREHOUSE_ADDRESS_2.equals(fromStreet)) {
            totalCost = totalCost.add(
                    DeliveryConstants.BASE_COST.multiply(DeliveryConstants.WAREHOUSE_ADDRESS_2_RATIO)
            );
        }

        if (Boolean.TRUE.equals(delivery.getFragile())) {
            totalCost = totalCost.add(totalCost.multiply(DeliveryConstants.FRAGILE_RATIO));
        }

        totalCost = totalCost.add(
                BigDecimal.valueOf(delivery.getDeliveryWeight())
                        .multiply(DeliveryConstants.WEIGHT_RATIO)
        );

        totalCost = totalCost.add(
                BigDecimal.valueOf(delivery.getDeliveryVolume())
                        .multiply(DeliveryConstants.VOLUME_RATIO)
        );

        if (!Objects.equals(fromStreet, toStreet)) {
            totalCost = totalCost.add(
                    totalCost.multiply(DeliveryConstants.DELIVERY_ADDRESS_RATIO)
            );
        }

        return totalCost;
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findDeliveryByDeliveryId(deliveryId)
                .orElseThrow(NoDeliveryFoundException::new);
    }
}