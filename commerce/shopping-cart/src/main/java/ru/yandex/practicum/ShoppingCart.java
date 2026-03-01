package ru.yandex.practicum;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
@Builder(toBuilder = true)
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id", nullable = false)
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    private Boolean active;

    @ElementCollection
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    @CollectionTable(name = "cart_products", joinColumns = @JoinColumn(name = "cart_id"))
    @Builder.Default
    private Map<UUID, Long> products = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private ShoppingCartStatus status;
}