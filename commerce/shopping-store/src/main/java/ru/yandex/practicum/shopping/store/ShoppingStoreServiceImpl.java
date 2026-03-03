package ru.yandex.practicum.shopping.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.enums.ProductState;
import ru.yandex.practicum.interaction.api.exception.ProductNotFoundException;
import ru.yandex.practicum.interaction.api.request.SetProductQuantityStateRequest;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productRepository.save(productMapper.mapToProduct(productDto));
        log.info("Новый Product сохранен в БД, entity: {}", product);

        return productMapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = findProductById(productDto.getProductId());
        productMapper.updateProductFromDto(product, productDto);

        Product updProduct = productRepository.save(product);
        log.info("Product обновлен, entity: {}", updProduct);

        return productMapper.mapToProductDto(updProduct);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = findProductById(productId);
        log.info("Получен Product, entity: {}", product);

        return productMapper.mapToProductDto(product);
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory productCategory, Pageable pageable) {
        Page<ProductDto> products = productRepository
                .findByProductCategoryAndProductState(productCategory, ProductState.ACTIVE, pageable)
                .map(productMapper::mapToProductDto);

        log.info("Получен список доступных товаров по категории {}: {}", productCategory, products);

        return products;
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = findProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        log.info("Удален Product: {}", product);

        return true;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = findProductById(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        log.info("Установлен статус количества: {}, для Product: {}", request.getQuantityState(), product);

        return true;
    }

    private Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product id = {} не найден", productId));
    }
}