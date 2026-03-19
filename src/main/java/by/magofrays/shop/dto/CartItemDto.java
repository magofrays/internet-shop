package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode
public class CartItemDto {
    @NotNull
    private UUID id;
    @NotNull
    private ItemDto item;
    @NotNull
    private Instant addedAt;
}
