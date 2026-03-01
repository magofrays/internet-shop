package by.magofrays.shop.service;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.ItemMapper;
import by.magofrays.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
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
        log.info("Creating item: {}", itemDto.getTitle());
        if(itemDto.getDiscountPrice() != null &&  itemDto.getDiscountPrice().compareTo(itemDto.getPrice()) > 0){
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        Item item = itemMapper.toEntity(itemDto);
        item.setId(UUID.randomUUID());
        itemRepository.save(item);
        if(image != null){
            setImageForItem(item, image);
        }
        log.debug("Created item: {}", itemDto.getId());
        itemDto.setId(item.getId());
        return itemDto;
    }

    public void setImageForItem(Item item, MultipartFile image){
        String url = fileStorageService.saveItemImage(image, item.getId());
        item.setImageUrl(url);
    }

    public ItemDto updateItem(ItemDto itemDto, MultipartFile image){
        log.info("Updating item: {}", itemDto.getId());
        if(itemDto.getId() == null){
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        if(itemDto.getDiscountPrice() != null &&  itemDto.getDiscountPrice().compareTo(itemDto.getPrice()) > 0){
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        item.setPrice(itemDto.getPrice());
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setQuantity(itemDto.getQuantity());
        item.setDiscountPrice(itemDto.getDiscountPrice());
        item.setUpdatedAt(Instant.now());
        if(image != null){
            setImageForItem(item, image);
        }
        return itemMapper.toDto(itemRepository.save(item));
    }



    public ItemDto findById(UUID itemId){
        log.debug("Request for item with id: {}", itemId);
        return itemMapper.toDto(
                itemRepository
                        .findById(itemId).orElseThrow(
                                () -> new BusinessException(HttpStatus.NOT_FOUND)
                        ));
    }

    public List<ItemDto> getAllItems(){
        log.debug("Request for all items");
        return itemRepository.findAll().stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

}
