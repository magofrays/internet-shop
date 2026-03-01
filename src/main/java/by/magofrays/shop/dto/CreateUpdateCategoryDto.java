package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder
public class CreateUpdateCategoryDto {
    private UUID id;
    @NotBlank
    private String title;
    private String description;
    private UUID parentCatalogueId;
}
