package ru.yandex.practicum.shopping.cart;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShoppingCartMapper {
    ShoppingCart mapToCart(ShoppingCartDto shoppingCartDto);

    ShoppingCartDto mapToCartDto(ShoppingCart shoppingCart);
}