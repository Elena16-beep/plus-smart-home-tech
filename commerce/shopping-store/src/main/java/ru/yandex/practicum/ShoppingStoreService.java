package ru.yandex.practicum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;
import java.util.UUID;

public interface ShoppingStoreService {
    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto getProduct(UUID productId);

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    boolean removeProductFromStore(UUID productId);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);
}