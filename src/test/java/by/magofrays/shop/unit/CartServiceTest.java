package by.magofrays.shop.unit;

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
import by.magofrays.shop.utils.LocalStorageTest;
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

    private LocalStorageTest lst;

    @BeforeEach
    void setUp() {
        lst = new LocalStorageTest();
    }

    @Test
    void getItemsInCart_ShouldReturnListOfDtos_WhenProfileExists() {
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));

        List<CartItemDto> result = cartService.getItemsInCart(lst.profileId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(lst.cartItemDto1, result.get(0));
        verify(profileRepository, times(1)).findById(lst.profileId);
    }

    @Test
    void getItemsInCart_ShouldReturnEmptyList_WhenCartHasNoItems() {
        lst.cart.setItemList(new ArrayList<>());
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));

        List<CartItemDto> result = cartService.getItemsInCart(lst.profileId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsInCart_WhenProfileNotFound_ShouldThrowBusinessException() {
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> cartService.getItemsInCart(lst.profileId));
        verify(profileRepository, times(1)).findById(lst.profileId);
    }


    @Test
    void addItemIntoCart_ShouldCreateCartItemAndReturnDto() {
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        when(itemRepository.findById(lst.item1.getId())).thenReturn(Optional.of(lst.item1));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(lst.cartItem1);

        CartItemDto result = cartService.addItemIntoCart(lst.item1.getId(), lst.profileId);

        assertNotNull(result);
        assertEquals(lst.cartItemDto1, result);
        verify(profileRepository, times(1)).findById(lst.profileId);
        verify(itemRepository, times(1)).findById(lst.item1.getId());
        verify(cartItemRepository, times(1)).save(argThat(savedCartItem ->
                savedCartItem.getCart().equals(lst.cart) && savedCartItem.getItem().equals(lst.item1)
        ));
    }

    @Test
    void addItemIntoCart_WhenProfileNotFound_ShouldThrowBusinessException() {
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> cartService.addItemIntoCart(lst.item1.getId(), lst.profileId));
        verify(profileRepository, times(1)).findById(lst.profileId);
    }

    @Test
    void addItemIntoCart_WhenItemNotFound_ShouldThrowBusinessException() {
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        when(itemRepository.findById(lst.item1.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> cartService.addItemIntoCart(lst.item1.getId(), lst.profileId));
        verify(profileRepository, times(1)).findById(lst.profileId);
        verify(itemRepository, times(1)).findById(lst.item1.getId());
    }

    @Test
    void removeItemFromCart_ShouldDeleteCartItemAndReturnDto() {
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        when(cartItemRepository.findById(lst.cartItem1.getId())).thenReturn(Optional.of(lst.cartItem1));
        cartService.removeItemFromCart(lst.cartItem1.getId(), lst.profileId);

        verify(cartItemRepository, times(1)).findById(lst.cartItem1.getId());
        verify(cartItemRepository, times(1)).delete(lst.cartItem1);
    }

    @Test
    void removeItemFromCart_WhenCartItemNotFound_ShouldThrowBusinessException() {
        when(cartItemRepository.findById(lst.cartItem1.getId())).thenReturn(Optional.empty());
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        assertThrows(BusinessException.class,
                () -> cartService.removeItemFromCart(lst.cartItem1.getId(), lst.profileId));
        verify(cartItemRepository, times(1)).findById(lst.cartItem1.getId());
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void createCartItem_ShouldSaveAndReturnCartItem() {
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(lst.cartItem1);

        CartItem result = cartService.createCartItem(lst.cart, lst.item1);

        assertNotNull(result);
        assertEquals(lst.cartItem1, result);
        verify(cartItemRepository, times(1)).save(argThat(savedCartItem ->
                savedCartItem.getCart().equals(lst.cart) && savedCartItem.getItem().equals(lst.item1)
        ));
    }
}