package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ItemDto {
    private UUID id;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private BigDecimal discountPrice;

    @NotNull
    @PositiveOrZero
    private Long quantity;
}
