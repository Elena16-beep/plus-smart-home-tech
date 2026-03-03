package ru.yandex.practicum.interaction.api.exception;

public class ShoppingCartNotFoundException extends RuntimeException {
    public ShoppingCartNotFoundException() {
    }

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }

    public ShoppingCartNotFoundException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}