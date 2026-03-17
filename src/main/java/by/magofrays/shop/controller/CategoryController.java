package by.magofrays.shop.controller;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.service.CategoryService;
import by.magofrays.shop.validation.UpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getRootCatalogues(){
        return ResponseEntity.ok(categoryService.getRootCatalogues());
    }

    @GetMapping("/{catalogueId}")
    public ResponseEntity<FullCategoryDto> getCatalogueTree(@PathVariable UUID catalogueId){
        return ResponseEntity.ok(
                categoryService.getCatalogueTree(catalogueId)
        );
    }

    @GetMapping("/{catalogueId}/children")
    public ResponseEntity<List<CategoryDto>> getChildren(@PathVariable UUID catalogueId){
        return ResponseEntity.ok(
                categoryService.getCategoriesByParentCategory(catalogueId)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDto> createCatalogue(@RequestBody @Validated CreateUpdateCategoryDto createCategoryDto){
        return ResponseEntity.ok(categoryService.createCatalogue(createCategoryDto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<CategoryDto> updateCatalogue(@RequestBody @Validated(UpdateGroup.class) CreateUpdateCategoryDto updateCategoryDto){
        return ResponseEntity.ok(
                categoryService.updateCatalogue(updateCategoryDto)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/item")
    public ResponseEntity<ItemDto> addItemIntoCatalogue(@RequestBody @Validated AddRemoveItemDto addItemDto){
        return ResponseEntity.ok(
                categoryService.addItemIntoCatalogue(addItemDto)
        );
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/item")
    public ResponseEntity<ItemDto> removeItemFromCatalogue(@RequestBody @Validated AddRemoveItemDto removeItemDto){
        return ResponseEntity.ok(
                categoryService.removeItemFromCatalogue(removeItemDto)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{catalogueId}")
    public ResponseEntity<?> deleteCatalogue(@PathVariable UUID catalogueId){
        categoryService.deleteCatalogueById(catalogueId);
        return ResponseEntity.noContent().build();
    }


}
