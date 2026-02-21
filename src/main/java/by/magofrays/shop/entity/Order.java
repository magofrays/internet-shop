package by.magofrays.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile createdBy;

    @OneToMany
    @Builder.Default
    private List<OrderItem> itemList = new ArrayList<>();

    private BigDecimal discountCost;
    private BigDecimal totalCost;

    private OrderStatus orderStatus;
    private String currency;

}
