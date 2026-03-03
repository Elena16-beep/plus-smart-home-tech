package ru.yandex.practicum.interaction.api.exception;

public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException() {
    }

    public NoProductsInShoppingCartException(String message) {
        super(message);
    }

    public NoProductsInShoppingCartException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}