package ru.yandex.practicum.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.api.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignClient {
    private final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("Добавление Product на склад, request: {}", request);
        warehouseService.newProductInWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        log.info("Проверка количества Product на складе для корзины, ShoppingCart: {}", cart);

        return warehouseService.checkProductQuantityEnoughForShoppingCart(cart);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Прием Product на склад, request: {}", request);
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");

        return warehouseService.getWarehouseAddress();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Передача товаров в доставку: {}", request);
        warehouseService.shippedToDelivery(request);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        log.info("Возврат товаров на склад: {}", products);
        warehouseService.acceptReturn(products);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.info("Сбор товаров к заказу: {}", request);

        return warehouseService.assemblyProductsForOrder(request);
    }
}