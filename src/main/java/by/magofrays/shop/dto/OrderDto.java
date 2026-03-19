package by.magofrays.shop.dto;

import by.magofrays.shop.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrderDto {

    private UUID id;

    private ReadProfileDto createdBy;

    @Builder.Default
    private List<OrderItemDto> itemList = new ArrayList<>();

    private BigDecimal discountCost;
    private BigDecimal totalCost;
    private OrderStatus orderStatus;
    private String currency;

    private Instant createdAt;
    private Instant updatedAt;
}
