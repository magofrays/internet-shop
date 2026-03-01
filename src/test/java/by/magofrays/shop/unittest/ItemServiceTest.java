package by.magofrays.shop.unittest;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.ItemMapperImpl;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.service.FileStorageService;
import by.magofrays.shop.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ItemService.class,
                ItemMapperImpl.class,
        }
)
public class ItemServiceTest {

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ItemService itemService;

    String imageUrl = "/tmp/upload/images/items/item-" + System.currentTimeMillis() + ".jpg";
    private UUID existingItemId;
    private Item existingItem;
    private ItemDto updateItemDto;

    @BeforeEach
    public void setUp() {
        Mockito.reset(itemRepository, fileStorageService);

        when(fileStorageService.saveItemImage(any(MultipartFile.class), any(UUID.class)))
                .thenReturn(imageUrl);

        existingItemId = UUID.randomUUID();
        existingItem = Item.builder()
                .id(existingItemId)
                .title("Старый Чехол")
                .description("Старое описание")
                .price(new BigDecimal("100.00"))
                .discountPrice(new BigDecimal("80.00"))
                .imageUrl("old/image/path.jpg")
                .quantity(5L)
                .createdAt(Instant.now().minus(1L, ChronoUnit.DAYS))
                .updatedAt(Instant.now().minus(1L, ChronoUnit.DAYS))
                .build();

        updateItemDto = ItemDto.builder()
                .id(existingItemId)
                .title("Новый Чехол")
                .description("Новое описание")
                .price(new BigDecimal("150.00"))
                .discountPrice(new BigDecimal("120.00"))
                .quantity(10L)
                .build();
    }

    @Test
    public void createItemTest(){
        ItemDto createItem = ItemDto.builder()
                .title("Чехол")
                .description("Чехол для Apple")
                .price(new BigDecimal("100.50"))
                .discountPrice(new BigDecimal("75.50"))
                .quantity(10L)
                .build();

        Item item = Item.builder()
                .id( UUID.randomUUID())
                .title("Чехол")
                .description("Чехол для Apple")
                .price(new BigDecimal("100.50"))
                .discountPrice(new BigDecimal("75.50"))
                .imageUrl(imageUrl)
                .quantity(10L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDto = itemService.createItem(createItem, new MockMultipartFile("www.jpg", (byte[]) null));
        verify(itemRepository).save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();
        Assertions.assertNotNull(savedItem);
        Assertions.assertNotNull(savedItem.getImageUrl(), "Image is not set");
        Assertions.assertNotNull(itemDto, "itemService.createItem() returned null");
        assertDtoAndEntity(savedItem, createItem);
        assertDtoAndEntity(item, itemDto);
    }

    @Test
    public void createItemTestWithError(){
        ItemDto createItem = ItemDto.builder()
                .title("Чехол")
                .description("Чехол для Apple")
                .price(new BigDecimal("100.50"))
                .discountPrice(new BigDecimal("175.50"))
                .quantity(10L)
                .build();
        Assertions.assertThrows(BusinessException.class, () -> itemService.createItem(createItem,
                new MockMultipartFile("www.jpg", (byte[]) null)));
    }

    private void assertDtoAndEntity(Item item, ItemDto itemDto){
        Assertions.assertEquals(itemDto.getTitle(), item.getTitle());
        Assertions.assertEquals(itemDto.getQuantity(), item.getQuantity());
        Assertions.assertEquals(itemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(itemDto.getPrice(), item.getPrice());
        Assertions.assertEquals(itemDto.getDiscountPrice(), item.getDiscountPrice());
    }

    @Test
    public void updateItemTest_ShouldUpdateAllFieldsWithoutImage() {
        when(itemRepository.findById(existingItemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.updateItem(updateItemDto, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateItemDto.getTitle(), result.getTitle());
        Assertions.assertEquals(updateItemDto.getDescription(), result.getDescription());
        Assertions.assertEquals(updateItemDto.getPrice(), result.getPrice());
        Assertions.assertEquals(updateItemDto.getDiscountPrice(), result.getDiscountPrice());
        Assertions.assertEquals(updateItemDto.getQuantity(), result.getQuantity());

        verify(itemRepository, times(1)).findById(existingItemId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(fileStorageService, never()).saveItemImage(any(), any());
    }

    @Test
    public void updateItemTest_ShouldUpdateWithNewImage() {
        MultipartFile newImage = new MockMultipartFile(
                "image",
                "new-image.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );

        when(itemRepository.findById(existingItemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.updateItem(updateItemDto, newImage);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateItemDto.getTitle(), result.getTitle());

        verify(itemRepository, times(1)).findById(existingItemId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(fileStorageService, times(1)).saveItemImage(eq(newImage), eq(existingItemId));
    }

    @Test
    public void updateItemTest_WhenIdIsNull_ShouldThrowException() {
        ItemDto dtoWithNullId = ItemDto.builder()
                .title("Чехол")
                .price(new BigDecimal("100.00"))
                .build();

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> itemService.updateItem(dtoWithNullId, null)
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(itemRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void updateItemTest_WhenDiscountPriceGreaterThanPrice_ShouldThrowException() {
        ItemDto invalidDto = ItemDto.builder()
                .id(existingItemId)
                .title("Чехол")
                .price(new BigDecimal("100.00"))
                .discountPrice(new BigDecimal("150.00")) // Больше цены
                .build();

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> itemService.updateItem(invalidDto, null)
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(itemRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void updateItemTest_WhenItemNotFound_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        ItemDto dto = ItemDto.builder()
                .id(nonExistentId)
                .title("Чехол")
                .price(new BigDecimal("100.00"))
                .build();

        when(itemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> itemService.updateItem(dto, null)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        verify(itemRepository, times(1)).findById(nonExistentId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void updateItemTest_ShouldUpdateOnlyProvidedFields() {
        ItemDto partialUpdateDto = ItemDto.builder()
                .id(existingItemId)
                .title("Только название изменено")
                .price(new BigDecimal("100.00")) // Та же цена
                .build();

        when(itemRepository.findById(existingItemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.updateItem(partialUpdateDto, null);

        Assertions.assertEquals("Только название изменено", result.getTitle());
        Assertions.assertEquals(existingItem.getDescription(), result.getDescription());
        Assertions.assertEquals(new BigDecimal("100.00"), result.getPrice());
        Assertions.assertEquals(existingItem.getQuantity(), result.getQuantity());
    }


    @Test
    public void findByIdTest_WhenItemExists_ShouldReturnItem() {
        when(itemRepository.findById(existingItemId)).thenReturn(Optional.of(existingItem));

        ItemDto result = itemService.findById(existingItemId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingItem.getId(), result.getId());
        Assertions.assertEquals(existingItem.getTitle(), result.getTitle());
        Assertions.assertEquals(existingItem.getDescription(), result.getDescription());
        Assertions.assertEquals(existingItem.getPrice(), result.getPrice());
        Assertions.assertEquals(existingItem.getDiscountPrice(), result.getDiscountPrice());
        Assertions.assertEquals(existingItem.getQuantity(), result.getQuantity());

        verify(itemRepository, times(1)).findById(existingItemId);
    }

    @Test
    public void findByIdTest_WhenItemDoesNotExist_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(itemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> itemService.findById(nonExistentId)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        verify(itemRepository, times(1)).findById(nonExistentId);
    }


    @Test
    public void getAllItemsTest_WhenItemsExist_ShouldReturnList() {
        Item item2 = Item.builder()
                .id(UUID.randomUUID())
                .title("Второй товар")
                .description("Описание 2")
                .price(new BigDecimal("200.00"))
                .quantity(20L)
                .build();

        List<Item> items = Arrays.asList(existingItem, item2);
        when(itemRepository.findAll()).thenReturn(items);

        List<ItemDto> result = itemService.getAllItems();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(existingItem.getId(), result.get(0).getId());
        Assertions.assertEquals(existingItem.getTitle(), result.get(0).getTitle());

        Assertions.assertEquals(item2.getId(), result.get(1).getId());
        Assertions.assertEquals(item2.getTitle(), result.get(1).getTitle());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void getAllItemsTest_WhenNoItems_ShouldReturnEmptyList() {
        when(itemRepository.findAll()).thenReturn(new ArrayList<>());

        List<ItemDto> result = itemService.getAllItems();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void getAllItemsTest_ShouldMapAllFieldsCorrectly() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(existingItem));

        List<ItemDto> result = itemService.getAllItems();

        Assertions.assertEquals(1, result.size());
        ItemDto dto = result.get(0);

        Assertions.assertEquals(existingItem.getId(), dto.getId());
        Assertions.assertEquals(existingItem.getTitle(), dto.getTitle());
        Assertions.assertEquals(existingItem.getDescription(), dto.getDescription());
        Assertions.assertEquals(existingItem.getPrice(), dto.getPrice());
        Assertions.assertEquals(existingItem.getDiscountPrice(), dto.getDiscountPrice());
        Assertions.assertEquals(existingItem.getQuantity(), dto.getQuantity());
    }
}
