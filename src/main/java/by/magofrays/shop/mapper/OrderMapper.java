package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, ProfileMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order order);
}
