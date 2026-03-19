package by.magofrays.shop.dto;

import by.magofrays.shop.validation.UpdateGroup;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class CreateUpdateCategoryDto {
    @NotNull(groups = UpdateGroup.class)
    private UUID id;
    @NotBlank
    private String title;
    private String description;
    private UUID parentCatalogueId;
}
