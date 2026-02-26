package by.magofrays.shop.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class FullCategoryDto {
    private UUID id;
    private String title;
    private String description;
    private List<FullCategoryDto> categoryList; // либо это
    private List<ItemDto> itemList; // либо это
    private Instant createdAt;
    private Instant updatedAt;
}
