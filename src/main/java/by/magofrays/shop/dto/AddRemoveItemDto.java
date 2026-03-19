package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class AddRemoveItemDto {
    @NotNull
    private UUID positionId;
    @NotNull
    private UUID itemId;
}
