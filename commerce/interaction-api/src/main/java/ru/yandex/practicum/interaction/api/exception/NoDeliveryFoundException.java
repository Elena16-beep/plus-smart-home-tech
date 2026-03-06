package ru.yandex.practicum.interaction.api.exception;

public class NoDeliveryFoundException extends RuntimeException {
    public NoDeliveryFoundException() {
    }

    public NoDeliveryFoundException(String message) {
        super(message);
    }

    public NoDeliveryFoundException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}