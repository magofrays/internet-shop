package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.CartDto;
import by.magofrays.shop.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    @Mapping(target = "profileId", source = "cart.profile.id")
    CartDto toDto(Cart cart);
}
