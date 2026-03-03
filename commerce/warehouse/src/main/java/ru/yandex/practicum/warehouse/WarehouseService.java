package ru.yandex.practicum.warehouse;

import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.request.NewProductInWarehouseRequest;

public interface WarehouseService {
    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart);

    AddressDto getWarehouseAddress();
}