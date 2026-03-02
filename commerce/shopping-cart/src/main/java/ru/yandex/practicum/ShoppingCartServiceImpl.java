package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseFeignClient;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
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

        if (cart.getStatus() == ShoppingCartStatus.DEACTIVATE) {
            throw new IllegalStateException("Корзина деактивирована: " + cart);
        }

        Map<UUID, Long> currentProducts = cart.getProducts();
        request.forEach((productId, quantity) -> currentProducts.merge(productId, quantity, Long::sum));
        warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(shoppingCartMapper.mapToCartDto(cart));

        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        log.info("Product добавлен в корзину: {}", savedCart);

        return shoppingCartMapper.mapToCartDto(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getShoppingCartByUser(username);

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

        if (!cart.getActive()) {
            throw new RuntimeException("Корзина деактивирована");
        }

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

        if (cart.getStatus() == ShoppingCartStatus.DEACTIVATE) {
            log.info("Текущее состояние корзины пользователя: {} - {}, повторная деактивация невозможна", username, cart.getStatus());

            return;
        }

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
                    .active(true)
                    .build();
            ShoppingCart savedCart = shoppingCartRepository.save(newCart);
            log.info("Создана новая корзина покупателя: {}", username);

            return savedCart;
        });
    }
}