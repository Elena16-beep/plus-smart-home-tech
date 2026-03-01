package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.enums.QuantityState;
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