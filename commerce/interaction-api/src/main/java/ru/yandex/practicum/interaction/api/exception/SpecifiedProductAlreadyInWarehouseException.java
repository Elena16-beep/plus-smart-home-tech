package ru.yandex.practicum.interaction.api.exception;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException() {
    }

    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }

    public SpecifiedProductAlreadyInWarehouseException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}