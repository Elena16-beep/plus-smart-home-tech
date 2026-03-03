package ru.yandex.practicum.shopping.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.api.exception.ShoppingCartDeactivateException;
import ru.yandex.practicum.interaction.api.exception.ShoppingCartNotFoundException;
import ru.yandex.practicum.interaction.api.request.ChangeProductQuantityRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> request) {
        ShoppingCart cart = getShoppingCartByUser(username);
        validateCartStatus(cart);
        Map<UUID, Long> currentProducts = cart.getProducts();
        request.forEach((productId, quantity) -> currentProducts.merge(productId, quantity, Long::sum));
        BookedProductsDto bookedProductsDto = warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(shoppingCartMapper.mapToCartDto(cart));
        log.info("Проверка на наличие товаров по параметрам: {}", bookedProductsDto);

        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        log.info("Product добавлен в корзину: {}", savedCart);

        return shoppingCartMapper.mapToCartDto(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getShoppingCartByUser(username);
        validateCartStatus(cart);

        if (request.getNewQuantity() > 0) {
            cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        } else {
            cart.getProducts().remove(request.getProductId());
        }

        ShoppingCart savedCart = shoppingCartRepository.save(cart);

        return shoppingCartMapper.mapToCartDto(savedCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart cart = getShoppingCartByUser(username);
        log.info("Получена корзина: {}", cart);

        return shoppingCartMapper.mapToCartDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        ShoppingCart cart = getShoppingCartByUser(username);
        validateCartStatus(cart);

        if (!cart.getProducts().keySet().containsAll(productIds)) {
            throw new NoProductsInShoppingCartException();
        }

        productIds.forEach(productId -> cart.getProducts().remove(productId));
        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        log.info("Товары удалены из корзины, savedCart: {}", savedCart);

        return shoppingCartMapper.mapToCartDto(savedCart);
    }

    @Override
    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        ShoppingCart cart = getShoppingCartByUser(username);
        validateCartStatus(cart);

        cart.setStatus(ShoppingCartStatus.DEACTIVATE);
        ShoppingCart savedCart = shoppingCartRepository.save(cart);

        log.info("Корзина пользователя: {} деактивирована, savedCart: {}", username, savedCart);
    }


    private ShoppingCart getShoppingCartByUser(String username) {
        if (username.isEmpty()) {
            throw new NotAuthorizedUserException();
        }

        return shoppingCartRepository.findByUsername(username).orElseGet(() -> {
            ShoppingCart newCart = ShoppingCart.builder()
                    .username(username)
                    .build();
            ShoppingCart savedCart = shoppingCartRepository.save(newCart);
            log.info("Создана новая корзина покупателя: {}", username);

            return savedCart;
        });
    }

    private void validateCartStatus(ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            throw new ShoppingCartNotFoundException();
        }

        if (shoppingCart.getStatus() == null) {
            throw new IllegalStateException("Статус корзины не определен");
        }

        if (shoppingCart.getStatus().equals(ShoppingCartStatus.DEACTIVATE)) {
            throw new ShoppingCartDeactivateException(("Корзина пользователя деактивирована"));
        }
    }
}