package ru.yandex.practicum.interaction.api.request;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.interaction.api.enums.QuantityState;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}