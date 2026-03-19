package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode
public class FullCategoryDto {
    private UUID id;
    private String title;
    private String description;
    @Builder.Default
    private List<FullCategoryDto> categoryList = new ArrayList<>(); // либо это
    @Builder.Default
    private List<ItemDto> itemList = new ArrayList<>(); // либо это
    private Instant createdAt;
    private Instant updatedAt;
}
