package by.magofrays.shop.dto;

import by.magofrays.shop.entity.OrderItem;
import by.magofrays.shop.entity.OrderStatus;
import by.magofrays.shop.entity.Profile;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private UUID id;

    private Profile createdBy;

    @Builder.Default
    private List<OrderItemDto> itemList = new ArrayList<>();

    private BigDecimal discountCost;
    private BigDecimal totalCost;

    private OrderStatus orderStatus;
    private String currency;
}
