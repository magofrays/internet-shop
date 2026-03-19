package by.magofrays.shop.module;

import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.utils.LocalStorageTest;
import by.magofrays.shop.utils.TokenGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRepository itemRepository;


    @Test
    @SneakyThrows
    public void createItemWithNoImage() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> {
            Item item = i.getArgument(0);
            item.setId(itemId);
            return item;
        });
        ItemDto itemDto = ItemDto.builder().title("Планшет IPAD").description("Производство Apple").price(new BigDecimal("1000.99")).discountPrice(new BigDecimal("800.99")).quantity(10L).build();
        String itemJson = objectMapper.writeValueAsString(itemDto);
        MockMultipartFile itemPart = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJson.getBytes());
        MvcResult result = mockMvc.perform(multipart("/api/item").file(itemPart).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).with(request -> {
            request.setMethod("POST");
            return request;
        })).andExpect(status().isOk()).andReturn();
        ItemDto itemDtoResult = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(itemDto.getTitle(), itemDtoResult.getTitle());
        assertEquals(itemDto.getDescription(), itemDtoResult.getDescription());
        assertEquals(itemDto.getPrice(), itemDtoResult.getPrice());
        assertEquals(itemDto.getDiscountPrice(), itemDtoResult.getDiscountPrice());
        assertEquals(itemDto.getQuantity(), itemDtoResult.getQuantity());
        assertEquals(itemId, itemDtoResult.getId());
    }

    @Test
    @SneakyThrows
    public void createItemWithImage() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> {
            Item item = i.getArgument(0);
            item.setId(itemId);
            return item;
        });
        ItemDto itemDto = ItemDto.builder().title("Планшет IPAD").description("Производство Apple").price(new BigDecimal("1000.99")).discountPrice(new BigDecimal("800.99")).quantity(10L).build();
        String itemJson = objectMapper.writeValueAsString(itemDto);
        MockMultipartFile itemPart = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJson.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        MvcResult result = mockMvc.perform(multipart("/api/item").file(itemPart)

                .file(imageFile).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).with(request -> {
                    request.setMethod("POST");
                    return request;
                })).andExpect(status().isOk()).andReturn();
        ItemDto itemDtoResult = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(itemDto.getTitle(), itemDtoResult.getTitle());
        assertEquals(itemDto.getDescription(), itemDtoResult.getDescription());
        assertEquals(itemDto.getPrice(), itemDtoResult.getPrice());
        assertEquals(itemDto.getDiscountPrice(), itemDtoResult.getDiscountPrice());
        assertEquals(itemDto.getQuantity(), itemDtoResult.getQuantity());
        assertEquals(itemId, itemDtoResult.getId());
    }

    @Test
    @SneakyThrows
    public void createItemWithError() {
        ItemDto itemDto = ItemDto.builder().title("Планшет IPAD").description("Производство Apple").price(new BigDecimal("1000.99")).discountPrice(new BigDecimal("800.99")).quantity(10L).build();
        String itemJson = objectMapper.writeValueAsString(itemDto);
        ItemDto itemDtoBad = ItemDto.builder().description("Производство Apple").discountPrice(new BigDecimal("-800.99")).quantity(-10L).build();
        String itemJsonBad = objectMapper.writeValueAsString(itemDtoBad);
        MockMultipartFile itemPartBad = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJsonBad.getBytes());
        MockMultipartFile itemPart = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJson.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "test-image.txt", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        MockMultipartFile goodImageFile = new MockMultipartFile("image", "test-image.txt", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        mockMvc.perform(multipart("/api/item").file(imageFile).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/item").file(itemPart).file(imageFile).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/item").file(itemPartBad).file(goodImageFile).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/item").file(itemPart).file(goodImageFile).header("Authorization", "Bearer " + tokenGenerator.getUserToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void updateItemWithNoImage() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));
        when(itemRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Item()));
        ItemDto itemDto = ItemDto.builder().id(itemId).title("Обновленный планшет").description("Обновленное описание").price(new BigDecimal("1200.99")).discountPrice(new BigDecimal("900.99")).quantity(5L).build();

        String itemJson = objectMapper.writeValueAsString(itemDto);
        MockMultipartFile itemPart = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJson.getBytes());

        MvcResult result = mockMvc.perform(multipart("/api/item").file(itemPart).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).with(request -> {
            request.setMethod("PUT");
            return request;
        })).andExpect(status().isOk()).andReturn();

        ItemDto itemDtoResult = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(itemDto.getTitle(), itemDtoResult.getTitle());
        assertEquals(itemDto.getDescription(), itemDtoResult.getDescription());
        assertEquals(itemDto.getPrice(), itemDtoResult.getPrice());
        assertEquals(itemDto.getDiscountPrice(), itemDtoResult.getDiscountPrice());
        assertEquals(itemDto.getQuantity(), itemDtoResult.getQuantity());
    }

    @Test
    @SneakyThrows
    public void updateItemWithImage() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));
        when(itemRepository.findById(any(UUID.class))).thenReturn(Optional.of(Item.builder().id(itemId).build()));
        ItemDto itemDto = ItemDto.builder().id(itemId).title("Обновленный планшет").description("Обновленное описание").price(new BigDecimal("1200.99")).discountPrice(new BigDecimal("900.99")).quantity(5L).build();

        String itemJson = objectMapper.writeValueAsString(itemDto);
        MockMultipartFile itemPart = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJson.getBytes());

        MockMultipartFile imageFile = new MockMultipartFile("image", "new-image.jpg", MediaType.IMAGE_JPEG_VALUE, "new image content".getBytes());

        MvcResult result = mockMvc.perform(multipart("/api/item").file(itemPart).file(imageFile).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).with(request -> {
            request.setMethod("PUT");
            return request;
        })).andExpect(status().isOk()).andReturn();
        ItemDto itemDtoResult = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(itemDto.getTitle(), itemDtoResult.getTitle());
    }

    @Test
    @SneakyThrows
    public void updateItem_ForbiddenForUser() {
        ItemDto itemDto = ItemDto.builder().id(UUID.randomUUID()).title("Обновленный планшет").build();

        String itemJson = objectMapper.writeValueAsString(itemDto);
        MockMultipartFile itemPart = new MockMultipartFile("item", "item.json", MediaType.APPLICATION_JSON_VALUE, itemJson.getBytes());

        mockMvc.perform(multipart("/api/item").file(itemPart).header("Authorization", "Bearer " + tokenGenerator.getUserToken()).contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).with(request -> {
            request.setMethod("PUT");
            return request;
        })).andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getItemById() {
        UUID itemId = UUID.randomUUID();
        ItemDto itemDto = ItemDto.builder().id(itemId).title("Планшет IPAD").description("Производство Apple").price(new BigDecimal("1000.99")).discountPrice(new BigDecimal("800.99")).quantity(10L).build();
        Item item = Item.builder().id(itemId).title(itemDto.getTitle()).description(itemDto.getDescription()).price(itemDto.getPrice()).discountPrice(itemDto.getDiscountPrice()).quantity(itemDto.getQuantity()).build();
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        MvcResult result = mockMvc.perform(get("/api/item/{itemId}", itemId).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        ItemDto itemDtoResult = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(itemDto, itemDtoResult);
    }

    @Test
    @SneakyThrows
    public void getItemById_NotFound() {
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(get("/api/item/{itemId}", itemId).header("Authorization", "Bearer " + tokenGenerator.getUserToken()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItems() {
        LocalStorageTest lst = new LocalStorageTest();
        List<Item> items = Arrays.asList(lst.item1, lst.item2);

        when(itemRepository.findAll()).thenReturn(items);
        List<ItemDto> itemDtos = Arrays.asList(lst.itemDto1, lst.itemDto2);
        MvcResult result = mockMvc.perform(get("/api/item").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        List<ItemDto> itemsResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ItemDto>>() {
        });
        assertEquals(itemDtos, itemsResult);
        assertEquals(2, itemsResult.size());
    }

    @Test
    @SneakyThrows
    public void getItemImage_NotFound() {
        UUID itemId = UUID.randomUUID();
        mockMvc.perform(get("/api/item/image/{itemId}", itemId)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteItem_ForbiddenForUser() {
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(delete("/api/item").header("Authorization", "Bearer " + tokenGenerator.getUserToken()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemId))).andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void deleteItem_WithInvalidId() {
        mockMvc.perform(delete("/api/item").header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.APPLICATION_JSON).content("\"invalid-uuid\"")).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void deleteItemIsNotFound() {
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(delete("/api/item").header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemId))).andExpect(status().isNotFound());
    }


    @Test
    @SneakyThrows
    public void setImageForItem() {
        UUID itemId = UUID.randomUUID();
        MockMultipartFile imageFile = new MockMultipartFile("image", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(Item.builder().id(itemId).build()));
        MockMultipartFile itemIdPart = new MockMultipartFile("item", "", MediaType.APPLICATION_JSON_VALUE, ("\"" + itemId + "\"").getBytes());

        mockMvc.perform(multipart("/api/item/image").file(itemIdPart).file(imageFile).header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deleteItemImageWithNoFound() {
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(delete("/api/item/image").header("Authorization", "Bearer " + tokenGenerator.getAdminToken()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemId))).andExpect(status().isNotFound());
    }
}
