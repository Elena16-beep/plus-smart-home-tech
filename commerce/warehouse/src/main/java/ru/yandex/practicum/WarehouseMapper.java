package ru.yandex.practicum;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {
    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct mapToEntity(NewProductInWarehouseRequest request);
}