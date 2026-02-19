package entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID orderItemId;

    @ManyToOne
    private Item item;
    @ManyToOne
    private Order order;
    private BigDecimal cost;
    private BigDecimal discountCost;

    private Instant createdAt;
}
