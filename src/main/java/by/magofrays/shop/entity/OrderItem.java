package by.magofrays.shop.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
    private BigDecimal cost;
    private BigDecimal discountCost;

    private Instant createdAt;
}
