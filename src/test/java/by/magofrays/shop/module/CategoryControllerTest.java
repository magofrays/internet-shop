package by.magofrays.shop.module;


import by.magofrays.shop.dto.AddRemoveItemDto;
import by.magofrays.shop.dto.CategoryDto;
import by.magofrays.shop.dto.CreateUpdateCategoryDto;
import by.magofrays.shop.dto.FullCategoryDto;
import by.magofrays.shop.entity.Category;
import by.magofrays.shop.repository.CategoryRepository;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.utils.LocalStorageTest;
import by.magofrays.shop.utils.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    LocalStorageTest lst;

    @Autowired
    TokenGenerator tokenGenerator;

    @MockBean
    ItemRepository itemRepository;

    @BeforeEach
    public void setup() {
        lst = new LocalStorageTest();
    }

    @Test
    @SneakyThrows
    public void getCatalogueTree() {
        when(categoryRepository.findById(eq(lst.rootCategory.getId()))).thenReturn(Optional.of(lst.rootCategory));
        MvcResult result = mockMvc.perform(
                        get("/api/category/" + lst.rootCategoryDto.getId())
                                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        FullCategoryDto resultDto = objectMapper.readValue(result.getResponse().getContentAsString(), FullCategoryDto.class);
        assertEquals(lst.fullRootCategoryDto, resultDto);
    }

    @Test
    @SneakyThrows
    public void getCatalogueTreeWithError() {
        mockMvc.perform(
                        get("/api/category/" + lst.rootCategoryDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getRootCataloguesTest() {
        when(categoryRepository.getCategoriesByParentCatalogue(null))
                .thenReturn(Collections.singletonList(lst.rootCategory));
        MvcResult result = mockMvc.perform(
                        get("/api/category")
                                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto[] categoryArray = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto[].class
        );
        assertEquals(lst.rootCategoryDto, categoryArray[0]);
    }

    @Test
    @SneakyThrows
    public void getRootCataloguesWithError() {
        mockMvc.perform(
                        get("/api/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getChildrenTest() {
        when(categoryRepository.findById(eq(lst.rootCategory.getId()))).thenReturn(Optional.of(lst.rootCategory));
        when(categoryRepository.findById(eq(lst.category1.getId()))).thenReturn(Optional.of(lst.category1));
        when(categoryRepository.getCategoriesByParentCatalogue(eq(lst.rootCategory)))
                .thenReturn(Collections.singletonList(lst.category1));
        when(categoryRepository.getCategoriesByParentCatalogue(eq(lst.category1)))
                .thenReturn(Collections.singletonList(lst.categoryKid1));
        MvcResult result = mockMvc.perform(get("/api/category/" + lst.rootCategoryDto.getId() + "/children")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();
        CategoryDto[] categories = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto[].class);
        assertEquals(lst.category1Dto, categories[0]);
        MvcResult result2 = mockMvc.perform(get("/api/category/" + lst.category1.getId() + "/children")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();
        CategoryDto[] categories2 = objectMapper.readValue(result2.getResponse().getContentAsString(), CategoryDto[].class);
        assertEquals(lst.categoryKid1Dto, categories2[0]);
    }

    @Test
    @SneakyThrows
    public void getChildrenWithError() {
        mockMvc.perform(get("/api/category/" + lst.rootCategoryDto.getId() + "/children")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void createCatalogue() {
        CreateUpdateCategoryDto createCategoryDto = CreateUpdateCategoryDto.builder()
                .title("Бытовая техника")
                .description("Различные бытовые приборы")
                .parentCatalogueId(lst.rootCategoryDto.getId())
                .build();
        when(categoryRepository.findById(eq(lst.rootCategoryDto.getId()))).thenReturn(Optional.of(lst.rootCategory));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(i -> i.getArgument(0));
        MvcResult mvcResult = mockMvc.perform(post("/api/category")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategoryDto))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        CategoryDto categoryDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(createCategoryDto.getTitle(), categoryDto.getTitle());
        assertEquals(createCategoryDto.getDescription(), categoryDto.getDescription());
    }

    @Test
    @SneakyThrows
    public void createCatalogueWithError() {
        CreateUpdateCategoryDto createCategoryDto = CreateUpdateCategoryDto.builder()
                .title("Бытовая техника")
                .description("Различные бытовые приборы")
                .parentCatalogueId(lst.rootCategoryDto.getId())
                .build();
        mockMvc.perform(post("/api/category")
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategoryDto))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategoryDto))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        createCategoryDto.setTitle("");
        mockMvc.perform(post("/api/category")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategoryDto))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void updateCatalogue() {
        CreateUpdateCategoryDto updateCategoryDto = CreateUpdateCategoryDto.builder()
                .id(lst.rootCategoryDto.getId())
                .title("Бытовые штучки")
                .description("Только бытовые приборы")
                .build();
        when(categoryRepository.findById(lst.rootCategoryDto.getId())).thenReturn(Optional.of(lst.rootCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        MvcResult result = mockMvc.perform(put("/api/category")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryDto))
                .characterEncoding("UTF-8")
        ).andExpect(status().isOk()).andReturn();
        CategoryDto resultDto = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(updateCategoryDto.getTitle(), resultDto.getTitle());
        assertEquals(updateCategoryDto.getDescription(), resultDto.getDescription());
    }

    @Test
    @SneakyThrows
    public void updateCatalogueWithError() {
        CreateUpdateCategoryDto updateCategoryDto = CreateUpdateCategoryDto.builder()
                .id(lst.rootCategoryDto.getId())
                .title("Бытовые штучки")
                .description("Только бытовые приборы")
                .build();
        mockMvc.perform(put("/api/category")
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryDto))
                .characterEncoding("UTF-8")).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryDto))
                .characterEncoding("UTF-8")).andExpect(status().isForbidden());
        updateCategoryDto.setId(null);
        mockMvc.perform(put("/api/category")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCategoryDto))
                .characterEncoding("UTF-8")).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void deleteCatalogue() {
        when(categoryRepository.findById(lst.rootCategoryDto.getId())).thenReturn(Optional.of(lst.rootCategory));
        mockMvc.perform(delete("/api/category/" + lst.rootCategoryDto.getId())
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deleteCatalogueWithError() {
        when(categoryRepository.findById(lst.rootCategoryDto.getId())).thenReturn(Optional.of(lst.rootCategory));
        mockMvc.perform(delete("/api/category/" + lst.rootCategoryDto.getId())
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isForbidden());
        when(categoryRepository.findById(lst.rootCategoryDto.getId())).thenReturn(Optional.of(lst.rootCategory));
        mockMvc.perform(delete("/api/category/" + lst.rootCategoryDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void addItemIntoCatalogueTest() {
        AddRemoveItemDto addRemoveItemDto = AddRemoveItemDto.builder()
                .itemId(lst.item2.getId())
                .positionId(lst.categoryKid1.getId())
                .build();
        when(categoryRepository.findById(lst.categoryKid1.getId())).thenReturn(Optional.of(lst.categoryKid1));
        System.out.println(lst.item2.getId());
        when(itemRepository.findById(lst.item2.getId())).thenReturn(Optional.of(lst.item2));
        mockMvc.perform(put("/api/category/item")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void addItemIntoCatalogueTestWithError() {
        AddRemoveItemDto addRemoveItemDto = AddRemoveItemDto.builder()
                .itemId(lst.item2.getId())
                .positionId(lst.category1.getId())
                .build();
        when(categoryRepository.findById(lst.category1.getId())).thenReturn(Optional.of(lst.category1));
        System.out.println(lst.item2.getId());
        when(itemRepository.findById(lst.item2.getId())).thenReturn(Optional.of(lst.item2));
        mockMvc.perform(put("/api/category/item")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/category/item")
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/category/item")
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void removeItemFromCatalogueTest() {
        AddRemoveItemDto addRemoveItemDto = AddRemoveItemDto.builder()
                .itemId(lst.item2.getId())
                .positionId(lst.categoryKid1.getId())
                .build();
        when(categoryRepository.findById(lst.categoryKid1.getId())).thenReturn(Optional.of(lst.categoryKid1));
        when(itemRepository.findById(lst.item2.getId())).thenReturn(Optional.of(lst.item2));
        mockMvc.perform(delete("/api/category/item")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void removeItemFromCatalogueTestWithError() {
        AddRemoveItemDto addRemoveItemDto = AddRemoveItemDto.builder()
                .itemId(lst.item2.getId())
                .positionId(lst.category1.getId())
                .build();
        when(categoryRepository.findById(lst.category1.getId())).thenReturn(Optional.of(lst.category1));
        when(itemRepository.findById(lst.item2.getId())).thenReturn(Optional.of(lst.item2));
        mockMvc.perform(delete("/api/category/item")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/category/item")
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/category/item")
                .content(objectMapper.writeValueAsString(addRemoveItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andExpect(status().isForbidden());

    }

}
