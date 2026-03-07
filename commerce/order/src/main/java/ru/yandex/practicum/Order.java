package ru.yandex.practicum;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction.api.enums.OrderState;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @ElementCollection
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private Map<UUID, Long> products;

    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "delivery_volume")
    private BigDecimal deliveryVolume;

    @Column(name = "delivery_weight")
    private BigDecimal deliveryWeight;

    private Boolean fragile;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice;
}