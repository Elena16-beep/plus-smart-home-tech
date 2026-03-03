package ru.yandex.practicum.interaction.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ApiError handleProductNotFound(ProductNotFoundException e) {
        log.warn("Product not found: {}", e.getMessage());
        return ApiError.fromException(e, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ApiError handleProductMissingInWarehouse(NoSpecifiedProductInWarehouseException e) {
        log.warn("Product missing in warehouse: {}", e.getMessage());
        return ApiError.fromException(e, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ShoppingCartNotFoundException.class)
    public ApiError handleShoppingCartNotFound(ShoppingCartNotFoundException e) {
        log.warn("ShoppingCart not found: {}", e.getMessage());
        return ApiError.fromException(e, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ApiError handleProductAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException e) {
        log.warn("Product already exists in warehouse: {}", e.getMessage());
        return ApiError.fromException(e, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ApiError handleInsufficientQuantity(ProductInShoppingCartLowQuantityInWarehouse e) {
        log.warn("Insufficient product quantity in warehouse: {}", e.getMessage());
        return ApiError.fromException(e, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleGenericException(Exception e, WebRequest request) {
        log.error("Unhandled exception at {}: {}", request.getDescription(false), e.getMessage(), e);
        return ApiError.fromException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}