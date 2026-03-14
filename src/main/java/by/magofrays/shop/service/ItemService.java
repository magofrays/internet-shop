package by.magofrays.shop.service;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.ItemMapper;
import by.magofrays.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final FileStorageService fileStorageService;

    public ItemDto createItem(ItemDto itemDto, MultipartFile image){
        log.debug("Creating item: {}", itemDto.getTitle());
        if(itemDto.getDiscountPrice() != null &&  itemDto.getDiscountPrice().compareTo(itemDto.getPrice()) > 0){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Discount price must be lower than price!");
        }
        Item item = itemMapper.toEntity(itemDto);
        item.setId(UUID.randomUUID());
        itemRepository.save(item);
        if(image != null){
            setImageForItem(item, image);
        }
        log.info("Created item: {}", itemDto.getId());
        itemDto.setId(item.getId());
        return itemDto;
    }

    public void setImageForItem(Item item, MultipartFile image){
        fileStorageService.validateImageFile(image);
        String url = fileStorageService.uploadFile(image, "images/item", item.getId(), item.getImageUrl());
        log.info("Setting new image: {} . For item: {}", item.getId(), url);
        item.setImageUrl(url);
    }

    public void setImageForItemId(UUID itemId, MultipartFile image){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!"));
        setImageForItem(item, image);
    }

    public void deleteItemImage(UUID itemId){
        String url = itemRepository.findById(itemId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!")).getImageUrl();
        fileStorageService.deleteFileForEntity(url, itemId);
    }

    public void deleteItem(UUID itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!"));
        try{
            deleteItemImage(itemId);
        }catch (BusinessException be){
            log.info("No image found, deleting item");
        }
        itemRepository.delete(item);
    }

    public ItemDto updateItem(ItemDto itemDto, MultipartFile image){
        log.debug("Updating item: {}", itemDto.getId());
        if(itemDto.getId() == null){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Item not found!");
        }
        if(itemDto.getDiscountPrice() != null &&  itemDto.getDiscountPrice().compareTo(itemDto.getPrice()) > 0){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Discount price must be lower than price!");
        }
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!"));
        item.setPrice(itemDto.getPrice());
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setQuantity(itemDto.getQuantity());
        item.setDiscountPrice(itemDto.getDiscountPrice());
        item.setUpdatedAt(Instant.now());
        if(image != null){
            setImageForItem(item, image);
        }
        item = itemRepository.save(item);
        log.info("Updated item: {}", item.getId());
        return itemMapper.toDto(item);
    }



    public ItemDto findById(UUID itemId){
        log.debug("Request for item with id: {}", itemId);
        return itemMapper.toDto(
                itemRepository
                        .findById(itemId).orElseThrow(
                                () -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!")
                        ));
    }

    public List<ItemDto> getAllItems(){
        log.debug("Request for all items");
        return itemRepository.findAll().stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public Resource getItemImage(UUID itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!"));
        if(item.getImageUrl() == null){
            throw new BusinessException(HttpStatus.NOT_FOUND, "Item does not have image!");
        }
        return fileStorageService.getFileByPath(item.getImageUrl());
    }

}
