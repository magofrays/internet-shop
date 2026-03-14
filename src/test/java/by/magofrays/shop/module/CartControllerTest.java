package by.magofrays.shop.module;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.entity.CartItem;
import by.magofrays.shop.repository.CartItemRepository;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.repository.ProfileRepository;
import by.magofrays.shop.utils.LocalStorageTest;
import by.magofrays.shop.utils.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartControllerTest  {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenGenerator tokenGenerator;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProfileRepository profileRepository;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    CartItemRepository cartItemRepository;

    LocalStorageTest lst;

    @BeforeEach
    void setUp() {
        lst = new LocalStorageTest();
    }

    @Test
    public void addItemIntoCartTest() throws Exception {

        when(profileRepository.findById(eq(UUID.fromString("33333333-3333-3333-3333-333333333333"))))
                .thenReturn(Optional.of(lst.profile));
        when(itemRepository.findById(eq(UUID.fromString("a0000000-0000-0000-0000-000000000003"))))
                .thenReturn(Optional.of(lst.item1));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        MvcResult result = mockMvc.perform(post("/api/cart")
                .header("Authorization", String.format("Bearer %s", tokenGenerator.getUserToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("\"a0000000-0000-0000-0000-000000000003\"")
        ).andExpect(status().isOk()).andReturn();
        CartItemDto cartItemDto = objectMapper.readValue(result.getResponse().getContentAsString(), CartItemDto.class);
        assertEquals(cartItemDto.getItem(), lst.cartItemDto1.getItem());
    }

    @Test
    public void addItemIntoCartTestWithError() throws Exception {
        mockMvc.perform(post("/api/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("\"a0000000-0000-0000-0000-000000000003\"")
        ).andExpect(status().isForbidden());
    }

    @Test
    public void removeItemFromCartTest() throws Exception {
        when(profileRepository.findById(eq(UUID.fromString("33333333-3333-3333-3333-333333333333"))))
                .thenReturn(Optional.of(lst.profile));
        when(cartItemRepository.findById(eq(lst.cartItem1.getId())))
                .thenReturn(Optional.of(lst.cartItem1));
        mockMvc.perform(delete("/api/cart")
                .header("Authorization", String.format("Bearer %s", tokenGenerator.getUserToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("\"%s\"",lst.cartItem1.getId().toString()))
        ).andExpect(status().isOk());
    }

    @Test
    public void removeItemFromCartTestWithError() throws Exception {
        mockMvc.perform(delete("/api/cart")
                .header("Authorization", String.format("Bearer %s", "asa21"))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("\"%s\"",lst.cartItem1.getId().toString())))
                .andExpect(status().isUnauthorized());
    }


}
