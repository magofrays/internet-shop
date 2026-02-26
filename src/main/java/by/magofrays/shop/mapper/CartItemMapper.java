package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);
}
