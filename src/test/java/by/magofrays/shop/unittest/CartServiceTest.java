package by.magofrays.shop.unittest;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.dto.ItemDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.CartItem;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CartItemMapperImpl;
import by.magofrays.shop.mapper.ItemMapperImpl;
import by.magofrays.shop.repository.CartItemRepository;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.repository.ProfileRepository;
import by.magofrays.shop.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CartService.class,
        CartItemMapperImpl.class,
        ItemMapperImpl.class
})
class CartServiceTest {

    @MockBean
    private CartItemRepository cartItemRepository;


    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CartService cartService;

    private UUID profileId;
    private UUID itemId;
    private UUID cartItemId;
    private Profile profile;
    private Cart cart;
    private Item item;
    private CartItem cartItem;
    private CartItemDto cartItemDto;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        cartItemId = UUID.randomUUID();

        cart = Cart.builder()
                .id(UUID.randomUUID())
                .build();

        profile = Profile.builder()
                .id(profileId)
                .cart(cart)
                .build();

        item = Item.builder()
                .id(itemId)
                .title("Test Item")
                .price(BigDecimal.TEN)
                .build();

        cartItem = CartItem.builder()
                .id(cartItemId)
                .cart(cart)
                .item(item)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .title(item.getTitle())
                .price(item.getPrice())
                .build();

        cartItemDto = CartItemDto.builder()
                .id(cartItemId)
                .item(itemDto)
                .build();
    }

    // ---- getItemsInCart ----
    @Test
    void getItemsInCart_ShouldReturnListOfDtos_WhenProfileExists() {
        // Given
        cart.setItemList(Collections.singletonList(cartItem));
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // When
        List<CartItemDto> result = cartService.getItemsInCart(profileId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cartItemDto, result.get(0));
        verify(profileRepository, times(1)).findById(profileId);
    }

    @Test
    void getItemsInCart_ShouldReturnEmptyList_WhenCartHasNoItems() {
        // Given
        cart.setItemList(new ArrayList<>());
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // When
        List<CartItemDto> result = cartService.getItemsInCart(profileId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsInCart_WhenProfileNotFound_ShouldThrowBusinessException() {
        // Given
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.getItemsInCart(profileId));
        verify(profileRepository, times(1)).findById(profileId);
    }

    // ---- addItemIntoCart ----
    @Test
    void addItemIntoCart_ShouldCreateCartItemAndReturnDto() {
        // Given
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // When
        CartItemDto result = cartService.addItemIntoCart(itemId, profileId);

        // Then
        assertNotNull(result);
        assertEquals(cartItemDto, result);
        verify(profileRepository, times(1)).findById(profileId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(cartItemRepository, times(1)).save(argThat(savedCartItem ->
                savedCartItem.getCart().equals(cart) && savedCartItem.getItem().equals(item)
        ));
    }

    @Test
    void addItemIntoCart_WhenProfileNotFound_ShouldThrowBusinessException() {
        // Given
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.addItemIntoCart(itemId, profileId));
        verify(profileRepository, times(1)).findById(profileId);
    }

    @Test
    void addItemIntoCart_WhenItemNotFound_ShouldThrowBusinessException() {
        // Given
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.addItemIntoCart(itemId, profileId));
        verify(profileRepository, times(1)).findById(profileId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    // ---- removeItemFromCart ----
    @Test
    void removeItemFromCart_ShouldDeleteCartItemAndReturnDto() {
        // Given
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        // When
        CartItemDto result = cartService.removeItemFromCart(cartItemId);

        // Then
        assertNotNull(result);
        assertEquals(cartItemDto, result);
        verify(cartItemRepository, times(1)).findById(cartItemId);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void removeItemFromCart_WhenCartItemNotFound_ShouldThrowBusinessException() {
        // Given
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.removeItemFromCart(cartItemId));
        verify(cartItemRepository, times(1)).findById(cartItemId);
        verify(cartItemRepository, never()).delete(any());
    }

    // ---- createCartItem (вспомогательный метод, но публичный) ----
    @Test
    void createCartItem_ShouldSaveAndReturnCartItem() {
        // Given
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // When
        CartItem result = cartService.createCartItem(cart, item);

        // Then
        assertNotNull(result);
        assertEquals(cartItem, result);
        verify(cartItemRepository, times(1)).save(argThat(savedCartItem ->
                savedCartItem.getCart().equals(cart) && savedCartItem.getItem().equals(item)
        ));
    }
}