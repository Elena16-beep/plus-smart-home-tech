package ru.yandex.practicum.exception;

public class AddProductToWarehouseRequest extends RuntimeException {
    public AddProductToWarehouseRequest() {
    }

    public AddProductToWarehouseRequest(String message) {
        super(message);
    }

    public AddProductToWarehouseRequest(String message, Object... args) {
        super(String.format(message.replace("{}", "%s"), args));
    }
}