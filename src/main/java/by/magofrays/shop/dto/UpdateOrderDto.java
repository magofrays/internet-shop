package by.magofrays.shop.dto;

import by.magofrays.shop.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UpdateOrderDto {
    private UUID orderId;
    private OrderStatus orderStatus;
    private List<CartItemDto> items;
}
