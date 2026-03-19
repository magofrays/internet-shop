package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.OrderItemDto;
import by.magofrays.shop.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);
}
