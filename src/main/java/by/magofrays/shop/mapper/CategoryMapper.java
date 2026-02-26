package by.magofrays.shop.mapper;

import by.magofrays.shop.dto.CategoryDto;
import by.magofrays.shop.dto.FullCategoryDto;
import by.magofrays.shop.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    FullCategoryDto toFullDto(Category category);
}
