package by.magofrays.shop.dto;

import by.magofrays.shop.entity.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UpdateOrderStatus {
    @NotNull
    private UUID orderId;
    @NotNull
    private OrderStatus orderStatus;
}
