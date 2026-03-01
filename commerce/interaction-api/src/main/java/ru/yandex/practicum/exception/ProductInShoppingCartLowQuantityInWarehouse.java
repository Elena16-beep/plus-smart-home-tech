package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouse() {
    }

    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super(message);
    }

    public ProductInShoppingCartLowQuantityInWarehouse(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}