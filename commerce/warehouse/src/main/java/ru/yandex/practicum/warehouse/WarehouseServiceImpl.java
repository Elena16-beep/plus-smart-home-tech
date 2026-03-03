package ru.yandex.practicum.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.ProductNotFoundException;
import ru.yandex.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.interaction.api.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.request.NewProductInWarehouseRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("На складе уже есть Product с id ",
                    request.getProductId());
        }

        warehouseRepository.save(warehouseMapper.mapToEntity(request));
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        validateProductQuantities(cart.getProducts());

        double weight = 0;
        double volume = 0;
        boolean fragile = false;

        Map<UUID, Long> cartProducts = cart.getProducts();
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            WarehouseProduct warehouseProduct = warehouseProducts.get(cartProduct.getKey());
            double productVolume = warehouseProduct.getDimension().getHeight() *
                    warehouseProduct.getDimension().getDepth() *
                    warehouseProduct.getDimension().getWidth();

            volume += productVolume * cartProduct.getValue();
            weight += warehouseProduct.getWeight() * cartProduct.getValue();

            if (warehouseProduct.getFragile()) {
                fragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryVolume(volume)
                .deliveryWeight(weight)
                .fragile(fragile)
                .build();
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new NoSpecifiedProductInWarehouseException("Нет информации о Product, id: ",
                                request.getProductId()));
        Long quantity = warehouseProduct.getQuantity();

        if (quantity == null) {
            quantity = 0L;
        }

        warehouseProduct.setQuantity(quantity + request.getQuantity());
        warehouseRepository.save(warehouseProduct);
        log.info("Количество Product на складе: {}", warehouseProduct.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = new Address().getAddress();

        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    private void validateProductQuantities(Map<UUID, Long> cartProducts) {
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        List<String> missingProducts = new ArrayList<>();
        List<String> insufficientProducts = new ArrayList<>();

        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            WarehouseProduct warehouseProduct = products.get(cartProduct.getKey());
            UUID productId = cartProduct.getKey();
            Long requestedQuantity = cartProduct.getValue();

            if (warehouseProduct == null) {
                missingProducts.add(String.format("Товар с id: %s", productId));
            } else if (requestedQuantity > warehouseProduct.getQuantity()) {
                insufficientProducts.add(String.format("Товар с id: %s (запрошено: %d, доступно: %d)",
                        productId, requestedQuantity, warehouseProduct.getQuantity()));
            }
        }

        if (!missingProducts.isEmpty()) {
            throw new ProductNotFoundException(
                    "Отсутствуют продукты на складе: " + String.join(", ", missingProducts),
                    missingProducts.stream()
                            .map(s -> UUID.fromString(s.split(": ")[1]))
                            .collect(Collectors.toList())
            );
        }

        if (!insufficientProducts.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Недостаточно продуктов на складе: " + String.join(", ", insufficientProducts),
                    insufficientProducts.stream()
                            .map(s -> UUID.fromString(s.split(": ")[1]))
                            .collect(Collectors.toList())
            );
        }
    }
}