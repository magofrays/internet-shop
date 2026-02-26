package by.magofrays.shop.unittest;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.ItemMapperImpl;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
    @Autowired
    private ItemService itemService;



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
                .quantity(10L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDto = itemService.createItem(createItem);
        verify(itemRepository).save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();
        Assertions.assertNotNull(savedItem);
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
        Item item = Item.builder()
                .id( UUID.randomUUID())
                .title("Чехол")
                .description("Чехол для Apple")
                .price(new BigDecimal("100.50"))
                .discountPrice(new BigDecimal("175.50"))
                .quantity(10L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item);
        Assertions.assertThrows(BusinessException.class, () -> itemService.createItem(createItem));
    }

    private void assertDtoAndEntity(Item item, ItemDto itemDto){
        Assertions.assertEquals(itemDto.getTitle(), item.getTitle());
        Assertions.assertEquals(itemDto.getQuantity(), item.getQuantity());
        Assertions.assertEquals(itemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(itemDto.getPrice(), item.getPrice());
        Assertions.assertEquals(itemDto.getDiscountPrice(), item.getDiscountPrice());
    }
}
