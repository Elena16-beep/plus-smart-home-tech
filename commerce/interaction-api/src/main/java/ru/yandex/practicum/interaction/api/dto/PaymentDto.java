package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.api.enums.PaymentState;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    @NotNull
    private UUID paymentId;

    private UUID orderId;

    private PaymentState status;

    private BigDecimal totalPayment;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;
}