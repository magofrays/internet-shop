package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);
    Item toEntity(ItemDto itemDto);
}
