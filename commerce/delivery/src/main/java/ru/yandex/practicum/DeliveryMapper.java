package ru.yandex.practicum;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DeliveryMapper {
    DeliveryDto mapToDto(Delivery delivery);

    @Mapping(target = "deliveryVolume",
            expression = "java(dto.getDeliveryVolume() != null ? dto.getDeliveryVolume() : 1.0)")
    @Mapping(target = "deliveryWeight",
            expression = "java(dto.getDeliveryWeight() != null ? dto.getDeliveryWeight() : 5.0)")
    @Mapping(target = "fragile",
            expression = "java(dto.getFragile() != null ? dto.getFragile() : false)")
    @Mapping(target = "fromAddress", source = "fromAddress")
    @Mapping(target = "toAddress", source = "toAddress")
    Delivery mapToEntity(DeliveryDto deliveryDto);

    @Mapping(target = "flat", expression = "java(dto.getFlat() != null ? dto.getFlat() : \"\")")
    Address mapAddressToEntity(AddressDto addressDto);
}