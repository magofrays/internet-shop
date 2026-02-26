package by.magofrays.shop.dto;

import by.magofrays.shop.entity.Item;
import by.magofrays.shop.entity.Order;
import lombok.Builder;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OrderItemDto {
    private UUID id;
    private Item item;
    private Order order;
    private BigDecimal cost;
    private BigDecimal discountCost;
    private Instant createdAt;
}
