package by.magofrays.shop.dto;

import by.magofrays.shop.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class UpdateOrderStatus {
    @NotNull
    private UUID orderId;
    @NotNull
    private OrderStatus orderStatus;
}
