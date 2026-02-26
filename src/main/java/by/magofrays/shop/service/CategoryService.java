package by.magofrays.shop.service;

import by.magofrays.shop.dto.CategoryDto;
import by.magofrays.shop.dto.FullCategoryDto;
import by.magofrays.shop.entity.Category;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CategoryMapper;
import by.magofrays.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getRootCatalogues(){
        return categoryRepository
                .getCategoriesByParentCatalogue(null).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public FullCategoryDto getCatalogueTree(UUID catalogueId){
        Category catalogue = categoryRepository.findById(catalogueId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND)
        );
        return categoryMapper.toFullDto(catalogue);
    }

    public List<CategoryDto> getCategoriesByParentCategory(UUID categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND)
                );
        return category.getCategoryList().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }




}
