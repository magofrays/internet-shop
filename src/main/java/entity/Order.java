package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private UUID orderId;

    @OneToMany
    @Builder.Default
    private List<OrderItem> itemList = new ArrayList<>();

    private BigDecimal discountAmount;
    private BigDecimal totalCost;

    private OrderStatus orderStatus;
    private String currency;
}
