package ru.yandex.practicum.shopping.cart;

import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.request.ChangeProductQuantityRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> request);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds);

    void deactivateCurrentShoppingCart(String username);
}