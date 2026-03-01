package ru.yandex.practicum.exception;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException() {
    }

    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }

    public NoSpecifiedProductInWarehouseException(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}