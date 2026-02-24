package by.magofrays.shop.dto;

import by.magofrays.shop.entity.CartItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private UUID profileId;
    private List<CartItemDto> itemList;
}
