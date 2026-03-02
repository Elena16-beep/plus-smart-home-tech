package ru.yandex.practicum.warehouse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.interaction.api.request.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {
    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct mapToEntity(NewProductInWarehouseRequest request);
}