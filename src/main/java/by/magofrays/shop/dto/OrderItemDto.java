package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode
public class OrderItemDto {
    private UUID id;
    private ItemDto item;
    private BigDecimal cost;
    private BigDecimal discountCost;
    private Instant createdAt;
}
