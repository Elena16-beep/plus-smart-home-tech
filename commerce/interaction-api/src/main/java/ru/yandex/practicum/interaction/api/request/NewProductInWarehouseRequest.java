package ru.yandex.practicum.interaction.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.api.dto.DimensionDto;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @Min(1)
    @NotNull
    private Double weight;
}