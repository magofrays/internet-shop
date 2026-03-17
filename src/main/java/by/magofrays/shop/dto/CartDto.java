package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CartDto {
    private UUID id;
    private UUID profileId;
    private List<CartItemDto> itemList;
}
