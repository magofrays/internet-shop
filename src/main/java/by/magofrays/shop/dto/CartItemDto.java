package by.magofrays.shop.dto;

import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Item;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode
public class CartItemDto {
    private UUID id;
    private ItemDto item;
    private Instant addedAt;
}
