package by.magofrays.shop.dto;

import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Item;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CartItemDto {
    private UUID id;
    private UUID cartId;
    private ItemDto item;
    private Instant addedAt;
}
