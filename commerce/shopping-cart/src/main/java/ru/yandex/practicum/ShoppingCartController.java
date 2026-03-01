package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.ShoppingCartFeignClient;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartFeignClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> request) {
        log.info("Добавление Product в ShoppingCart, request: {}", request);
        return shoppingCartService.addProductToShoppingCart(username, request);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Изменение количества Product в ShoppingCart, request: {}", request);
        return shoppingCartService.changeProductQuantity(username, request);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Получение ShoppingCart для пользователя, username: {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        log.info("Удаление Product из ShoppingCart, productIds: {}", productIds);
        return shoppingCartService.removeFromShoppingCart(username, productIds);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        log.info("Деактивация ShoppingCart пользователя, username: {}", username);
        shoppingCartService.deactivateCurrentShoppingCart(username);
    }
}