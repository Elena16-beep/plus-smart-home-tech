package ru.yandex.practicum.shopping.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.client.ShoppingStoreFeignClient;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.request.SetProductQuantityStateRequest;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreFeignClient {
    private final ShoppingStoreServiceImpl shoppingStoreService;

    @Override
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Создание Product dto: {}", productDto);

        return shoppingStoreService.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Обновление Product dto: {}", productDto);

        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public ProductDto getProduct(@NotNull @PathVariable UUID productId) {
        log.info("Получение Product id: {}", productId);

        return shoppingStoreService.getProduct(productId);
    }

    @Override
    public Page<ProductDto> getProducts(@NotNull @RequestParam("category") ProductCategory category,
                                        @Valid @SpringQueryMap Pageable pageable) {
        log.info("Получение списка Product по категории: {}", category);

        return shoppingStoreService.getProducts(category, pageable);
    }

    @Override
    public boolean removeProductFromStore(@NotNull @RequestBody UUID productId) {
        log.info("Удаление Product id: {}", productId);

        return shoppingStoreService.removeProductFromStore(productId);
    }

    @Override
    public boolean setProductQuantityState(@Valid @ModelAttribute SetProductQuantityStateRequest request) {
        log.info("Обновление статуса количества Product request: {}", request);
        
        return shoppingStoreService.setProductQuantityState(request);
    }
}