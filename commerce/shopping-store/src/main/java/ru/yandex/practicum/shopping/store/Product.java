package ru.yandex.practicum.shopping.store;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.enums.ProductState;
import ru.yandex.practicum.interaction.api.enums.QuantityState;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Builder(toBuilder = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    private String description;

    @Column(name = "image_src", nullable = false)
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state")
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state")
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category")
    private ProductCategory productCategory;

    @Min(1)
    private Float price;
}