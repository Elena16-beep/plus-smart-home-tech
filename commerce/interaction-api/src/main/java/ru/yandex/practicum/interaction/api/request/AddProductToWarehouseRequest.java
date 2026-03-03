package ru.yandex.practicum.interaction.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;

    @Min(1)
    @NotNull
    private Integer quantity;
}