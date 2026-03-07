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
            expression = "java(deliveryDto.getDeliveryVolume() != null ? deliveryDto.getDeliveryVolume() : 1.0)")
    @Mapping(target = "deliveryWeight",
            expression = "java(deliveryDto.getDeliveryWeight() != null ? deliveryDto.getDeliveryWeight() : 5.0)")
    @Mapping(target = "fragile",
            expression = "java(deliveryDto.getFragile() != null ? deliveryDto.getFragile() : false)")
    @Mapping(target = "fromAddress", source = "fromAddress")
    @Mapping(target = "toAddress", source = "toAddress")
    Delivery mapToEntity(DeliveryDto deliveryDto);

    @Mapping(target = "flat", expression = "java(addressDto.getFlat() != null ? addressDto.getFlat() : \"\")")
    Address mapAddressToEntity(AddressDto addressDto);
}