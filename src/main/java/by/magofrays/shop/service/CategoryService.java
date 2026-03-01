package by.magofrays.shop.service;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.entity.Category;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CategoryMapper;
import by.magofrays.shop.mapper.ItemMapper;
import by.magofrays.shop.repository.CategoryRepository;
import by.magofrays.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public List<CategoryDto> getRootCatalogues(){
        return categoryRepository
                .getCategoriesByParentCatalogue(null).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto addItemIntoCatalogue(AddRemoveItemDto addItemDto){
        Category catalogue = categoryRepository.findById(addItemDto.getPositionId()).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        if(!catalogue.getCategoryList().isEmpty()){
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        Item item = itemRepository.findById(addItemDto.getItemId()).orElseThrow(() ->new BusinessException(HttpStatus.NOT_FOUND));
        catalogue.addItem(item);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto removeItemFromCatalogue(AddRemoveItemDto removeItemDto){
        Item item = itemRepository.findById(removeItemDto.getItemId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Category catalogue = categoryRepository
                .findById(removeItemDto.getPositionId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        List<Item> items = catalogue.getItemList();
        if(!items.contains(item)){
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        items.remove(item);
        item.getCategories().remove(catalogue);
        return itemMapper.toDto(itemRepository.save(item));
    }


    @Transactional
    public CategoryDto createCatalogue(CreateUpdateCategoryDto createCategoryDto){
        if(createCategoryDto.getParentCatalogueId() == null){
            return categoryMapper.toDto(
                    categoryRepository.save(
                            categoryMapper.toEntity(createCategoryDto)
                    ));
        } else {
            Category parentCatalogue = categoryRepository
                    .findById(createCategoryDto.getParentCatalogueId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
            if(!parentCatalogue.getItemList().isEmpty()){
                throw new BusinessException(HttpStatus.BAD_REQUEST);
            }
            Category category = categoryMapper.toEntity(createCategoryDto);
            categoryRepository.save(category);
            parentCatalogue.addChildCatalogue(category);
            return categoryMapper.toDto(category);
        }
    }

    @Transactional
    public CategoryDto updateCatalogue(CreateUpdateCategoryDto updateCategoryDto){
        Category category = categoryRepository
                .findById(updateCategoryDto.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Category parentCatalogue = category.getParentCatalogue();
        if(parentCatalogue.getId() != updateCategoryDto.getParentCatalogueId()){
            Category newParent = categoryRepository
                    .findById(updateCategoryDto.getParentCatalogueId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
            parentCatalogue.getCategoryList().remove(category);
            newParent.addChildCatalogue(category);
        }
        category.setTitle(updateCategoryDto.getTitle());
        category.setDescription(updateCategoryDto.getDescription());
        return categoryMapper.toDto(categoryRepository.save(category));
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
