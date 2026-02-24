package by.magofrays.shop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemDto {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Long quantity;
}
