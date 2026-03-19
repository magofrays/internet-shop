package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UpdateOrderDto {
    @NotNull
    private UUID orderId;
    @NotEmpty
    private List<CartItemDto> items;
}
