package by.magofrays.shop.controller;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDto> createItem(
            @RequestPart("item") @Validated ItemDto itemDto,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(itemService.createItem(itemDto, image));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/image")
    public ResponseEntity<?> setImageForItem(
            @RequestPart("item") UUID itemId,
            @RequestParam("image") MultipartFile image) {
        itemService.setImageForItemId(itemId, image);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/image")
    public ResponseEntity<?> deleteItemImage(@RequestBody UUID itemId) {
        itemService.deleteItemImage(itemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    public ResponseEntity<?> deleteItem(@RequestBody UUID itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<ItemDto> updateItem(
            @RequestPart("item") ItemDto itemDto,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        ItemDto result = itemService.updateItem(itemDto, image);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable UUID itemId) {
        ItemDto result = itemService.findById(itemId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems() {
        return ResponseEntity.ok(
                itemService.getAllItems()
        );
    }

    @GetMapping("/image/{itemId}")
    public ResponseEntity<Resource> getItemImage(@PathVariable UUID itemId) {
        return ResponseEntity.ok(
                itemService.getItemImage(itemId)
        );
    }
}

