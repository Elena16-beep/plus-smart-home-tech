package ru.yandex.practicum.interaction.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.request.SetProductQuantityStateRequest;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreFeignClient {
    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @GetMapping("/{productId}")
    ProductDto getProduct(@NotNull @PathVariable UUID productId);

    @GetMapping
    Page<ProductDto> getProducts(@NotNull @RequestParam("category") ProductCategory category,
                                 @Valid @SpringQueryMap Pageable pageable);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@NotNull @RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@Valid @ModelAttribute SetProductQuantityStateRequest request);
}