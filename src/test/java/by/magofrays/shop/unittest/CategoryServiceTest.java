package by.magofrays.shop.unittest;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.entity.Category;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CategoryMapperImpl;
import by.magofrays.shop.mapper.ItemMapperImpl;
import by.magofrays.shop.repository.CategoryRepository;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CategoryService.class,
        CategoryMapperImpl.class,
        ItemMapperImpl.class
})
public class CategoryServiceTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CategoryService categoryService;

    private UUID categoryId;
    private UUID parentId;
    private UUID itemId;
    private Category category;
    private Category parentCategory;
    private Item item;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        parentId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        parentCategory = Category.builder()
                .id(parentId)
                .title("Parent")
                .description("Parent desc")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        category = Category.builder()
                .id(categoryId)
                .title("Child")
                .description("Child desc")
                .parentCatalogue(parentCategory)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        item = Item.builder()
                .id(itemId)
                .title("Item")
                .description("Item desc")
                .price(BigDecimal.TEN)
                .quantity(5L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ---- getRootCatalogues ----
    @Test
    void getRootCatalogues_ShouldReturnListOfDtos_WhenRootCategoriesExist() {
        when(categoryRepository.getCategoriesByParentCatalogue(null)).thenReturn(Collections.singletonList(category));

        List<CategoryDto> result = categoryService.getRootCatalogues();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(categoryId, result.get(0).getId());
        assertEquals("Child", result.get(0).getTitle());
        verify(categoryRepository, times(1)).getCategoriesByParentCatalogue(null);
    }

    @Test
    void getRootCatalogues_ShouldReturnEmptyList_WhenNoRootCategories() {
        when(categoryRepository.getCategoriesByParentCatalogue(null)).thenReturn(new ArrayList<>());

        List<CategoryDto> result = categoryService.getRootCatalogues();

        assertTrue(result.isEmpty());
    }

    // ---- createCatalogue ----
    @Test
    void createCatalogue_WithoutParent_ShouldSaveAndReturnDto() {
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .title("New Root")
                .description("New Desc")
                .parentCatalogueId(null)
                .build();

        Category savedCategory = Category.builder()
                .id(UUID.randomUUID())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDto result = categoryService.createCatalogue(dto);

        assertNotNull(result);
        assertEquals(savedCategory.getId(), result.getId());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getDescription(), result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void createCatalogue_WithParent_ShouldAddChildAndReturnDto() {
        UUID parentId = UUID.randomUUID();
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .title("Child")
                .description("Child Desc")
                .parentCatalogueId(parentId)
                .build();

        Category parent = Category.builder()
                .id(parentId)
                .title("Parent")
                .itemList(new ArrayList<>()) // пустой список товаров
                .build();

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDto result = categoryService.createCatalogue(dto);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        // Проверим, что родитель добавил ребёнка
        assertEquals(1, parent.getCategoryList().size());
        assertEquals(result.getId(), parent.getCategoryList().get(0).getId());
        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCatalogue_WithParentNotFound_ShouldThrowBusinessException() {
        UUID parentId = UUID.randomUUID();
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .parentCatalogueId(parentId)
                .build();

        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.createCatalogue(dto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void createCatalogue_WhenParentHasItems_ShouldThrowBusinessException() {
        UUID parentId = UUID.randomUUID();
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .parentCatalogueId(parentId)
                .build();

        Category parent = Category.builder()
                .id(parentId)
                .itemList(Collections.singletonList(new Item())) // не пустой список
                .build();

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.createCatalogue(dto));
        verify(categoryRepository, never()).save(any());
    }

    // ---- updateCatalogue ----
    @Test
    void updateCatalogue_ShouldUpdateFieldsAndChangeParent_WhenParentChanged() {
        UUID newParentId = UUID.randomUUID();
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .id(categoryId)
                .title("Updated Title")
                .description("Updated Desc")
                .parentCatalogueId(newParentId)
                .build();

        Category newParent = Category.builder()
                .id(newParentId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(newParentId)).thenReturn(Optional.of(newParent));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.updateCatalogue(dto);

        assertEquals(dto.getTitle(), category.getTitle());
        assertEquals(dto.getDescription(), category.getDescription());
        // Проверяем, что родитель изменился
        assertTrue(parentCategory.getCategoryList().isEmpty()); // старый родитель потерял ребёнка
        assertEquals(1, newParent.getCategoryList().size());
        assertEquals(category, newParent.getCategoryList().get(0));
        verify(categoryRepository, times(2)).findById(any());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCatalogue_WhenParentNotChanged_ShouldOnlyUpdateFields() {
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .id(categoryId)
                .title("Updated Title")
                .description("Updated Desc")
                .parentCatalogueId(parentId) // тот же родитель
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        // Не мокаем повторный findById, так как parent не меняется, и второй раз не вызывается
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.updateCatalogue(dto);

        assertEquals(dto.getTitle(), category.getTitle());
        assertEquals(dto.getDescription(), category.getDescription());
        // Проверяем, что родитель не менялся
        assertEquals(parentCategory, category.getParentCatalogue());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).findById(parentId); // второй раз не вызывался
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCatalogue_WhenCategoryNotFound_ShouldThrow() {
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .id(categoryId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.updateCatalogue(dto));
    }

    @Test
    void updateCatalogue_WhenNewParentNotFound_ShouldThrow() {
        UUID newParentId = UUID.randomUUID();
        CreateUpdateCategoryDto dto = CreateUpdateCategoryDto.builder()
                .id(categoryId)
                .parentCatalogueId(newParentId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(newParentId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.updateCatalogue(dto));
    }

    // ---- addItemIntoCatalogue ----
    @Test
    void addItemIntoCatalogue_ShouldAddItem_WhenCatalogueHasNoSubcategories() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .itemId(itemId)
                .build();

        // Категория без дочерних категорий
        category.setCategoryList(new ArrayList<>());
        category.setItemList(new ArrayList<>());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = categoryService.addItemIntoCatalogue(dto);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertTrue(category.getItemList().contains(item));
        assertTrue(item.getCategories().contains(category));
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void addItemIntoCatalogue_WhenCatalogueHasSubcategories_ShouldThrow() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .itemId(itemId)
                .build();

        category.setCategoryList(Collections.singletonList(new Category())); // есть подкатегории

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.addItemIntoCatalogue(dto));
        verify(itemRepository, never()).findById(any());
    }

    @Test
    void addItemIntoCatalogue_WhenCategoryNotFound_ShouldThrow() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.addItemIntoCatalogue(dto));
    }

    @Test
    void addItemIntoCatalogue_WhenItemNotFound_ShouldThrow() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .itemId(itemId)
                .build();

        category.setCategoryList(new ArrayList<>());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.addItemIntoCatalogue(dto));
    }

    // ---- removeItemFromCatalogue ----
    @Test
    void removeItemFromCatalogue_ShouldRemoveItem_WhenItemExistsInCatalogue() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .itemId(itemId)
                .build();

        category.setItemList(new ArrayList<>(Collections.singletonList(item)));
        item.setCategories(new ArrayList<>(Collections.singletonList(category)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = categoryService.removeItemFromCatalogue(dto);

        assertNotNull(result);
        assertFalse(category.getItemList().contains(item));
        assertFalse(item.getCategories().contains(category));
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void removeItemFromCatalogue_WhenItemNotInCatalogue_ShouldThrow() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .itemId(itemId)
                .build();

        category.setItemList(new ArrayList<>()); // нет товара

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.removeItemFromCatalogue(dto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void removeItemFromCatalogue_WhenItemNotFound_ShouldThrow() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .itemId(itemId)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.removeItemFromCatalogue(dto));
    }

    @Test
    void removeItemFromCatalogue_WhenCategoryNotFound_ShouldThrow() {
        AddRemoveItemDto dto = AddRemoveItemDto.builder()
                .positionId(categoryId)
                .itemId(itemId)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.removeItemFromCatalogue(dto));
    }

    // ---- getCatalogueTree ----
    @Test
    void getCatalogueTree_ShouldReturnFullDto_WhenCategoryExists() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        FullCategoryDto result = categoryService.getCatalogueTree(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals(category.getTitle(), result.getTitle());
        // Дочерние категории и товары мапятся рекурсивно, но в данном случае у category их нет
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getCatalogueTree_WhenCategoryNotFound_ShouldThrow() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.getCatalogueTree(categoryId));
    }

    // ---- getCategoriesByParentCategory ----
    @Test
    void getCategoriesByParentCategory_ShouldReturnListOfDtos() {
        Category child1 = Category.builder().id(UUID.randomUUID()).title("Child1").build();
        Category child2 = Category.builder().id(UUID.randomUUID()).title("Child2").build();
        parentCategory.setCategoryList(Arrays.asList(child1, child2));

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));

        List<CategoryDto> result = categoryService.getCategoriesByParentCategory(parentId);

        assertEquals(2, result.size());
        assertEquals("Child1", result.get(0).getTitle());
        assertEquals("Child2", result.get(1).getTitle());
    }

    @Test
    void getCategoriesByParentCategory_WhenParentNotFound_ShouldThrow() {
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.getCategoriesByParentCategory(parentId));
    }
}