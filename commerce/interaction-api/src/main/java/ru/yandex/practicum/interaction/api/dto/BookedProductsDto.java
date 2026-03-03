package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {
    @NotNull
    private Double deliveryWeight;

    @NotNull
    private Double deliveryVolume;

    @NotNull
    private Boolean fragile;
}