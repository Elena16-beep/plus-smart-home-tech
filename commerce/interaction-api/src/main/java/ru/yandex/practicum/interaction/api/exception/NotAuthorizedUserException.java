package ru.yandex.practicum.interaction.api.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException() {
    }

    public NotAuthorizedUserException(String message) {
        super(message);
    }

    public NotAuthorizedUserException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}