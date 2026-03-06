package ru.yandex.practicum.interaction.api.exception;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException() {
    }

    public NoOrderFoundException(String message) {
        super(message);
    }

    public NoOrderFoundException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}