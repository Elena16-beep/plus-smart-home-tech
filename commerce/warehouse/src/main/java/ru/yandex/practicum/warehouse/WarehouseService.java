package ru.yandex.practicum.warehouse;

import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.api.request.ShippedToDeliveryRequest;
import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> products);
}