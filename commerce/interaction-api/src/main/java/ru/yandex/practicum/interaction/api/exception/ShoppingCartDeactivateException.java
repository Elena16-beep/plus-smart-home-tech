package ru.yandex.practicum.interaction.api.exception;

public class ShoppingCartDeactivateException extends RuntimeException {
    public ShoppingCartDeactivateException() {
    }

    public ShoppingCartDeactivateException(String message) {
        super(message);
    }

    public ShoppingCartDeactivateException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}