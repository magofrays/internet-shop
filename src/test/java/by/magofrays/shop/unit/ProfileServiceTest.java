package by.magofrays.shop.unit;

import by.magofrays.shop.dto.CartDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.dto.UpdateProfileDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Order;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.*;
import by.magofrays.shop.repository.CartRepository;
import by.magofrays.shop.repository.OrderRepository;
import by.magofrays.shop.repository.ProfileRepository;
import by.magofrays.shop.service.OrderService;
import by.magofrays.shop.service.ProfileService;
import by.magofrays.shop.utils.LocalStorageTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ProfileMapperImpl.class,
                CartMapperImpl.class,
                CartItemMapperImpl.class,
                ItemMapperImpl.class,
                OrderMapperImpl.class,
                OrderItemMapperImpl.class,
                ProfileMapperImpl.class,
                ProfileService.class
        }
)
public class ProfileServiceTest {
    @Autowired
    ProfileService profileService;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    OrderService orderService;

    @MockBean
    ProfileRepository profileRepository;

    @MockBean
    CartRepository cartRepository;

    Profile profile;
    Cart cart;
    private Order order;
    private OrderDto orderDto;
    private CartDto cartDto;
    private LocalStorageTest lst;

    @BeforeEach
    public void init(){
        lst = new LocalStorageTest();
        profile = lst.profile;
        cart = lst.cart;
        order = lst.order;
        orderDto = lst.orderDto;
        cartDto = lst.cartDto;
    }

    @Test
    public void getOrdersTest(){
        when(profileRepository.existsProfileById(eq(profile.getId()))).thenReturn(true);
        when(orderRepository.findByCreatedBy_Id(eq(profile.getId()))).thenReturn(Collections.singletonList(order));
        List<OrderDto> orderDtos = profileService.getOrders(profile.getId());
        assertEquals(orderDto, orderDtos.get(0));
    }

    @Test
    public void getOrdersWithErrorTest(){
        when(profileRepository.existsProfileById(eq(profile.getId()))).thenReturn(false);
        assertThrows(BusinessException.class, () -> profileService.getOrders(profile.getId()));
    }

    @Test
    public void getEmptyOrdersTest(){
        when(profileRepository.existsProfileById(eq(profile.getId()))).thenReturn(true);
        when(orderRepository.findByCreatedBy_Id(eq(profile.getId()))).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), profileService.getOrders(profile.getId()));
    }

    @Test
    public void getCartTest(){
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.of(profile));
        CartDto result = profileService.getCart(profile.getId());
        assertEquals(cartDto, result);
    }

    @Test
    public void getCartWithErrorTest(){
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> profileService.getCart(profile.getId()));
    }

    @Test
    public void getEmptyCartTest(){
        cart.setItemList(Collections.emptyList());
        cartDto.setItemList(Collections.emptyList());
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.of(profile));
        CartDto result = profileService.getCart(profile.getId());
        assertEquals(cartDto, result);
    }

    @Test
    public void getProfileInfoTest(){
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.ofNullable(profile));
        ReadProfileDto result = profileService.getProfileInfo(profile.getId());
        assertEquals(lst.profileDto, result);
    }

    @Test
    public void getProfileInfoWithErrorTest(){
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> profileService.getProfileInfo(profile.getId()));
    }

    @Test
    public void updateProfileTest(){
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.ofNullable(profile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(newProfile -> newProfile.getArgument(0));
        UpdateProfileDto req = UpdateProfileDto.builder()
                .id(profile.getId())
                .firstname("Vanya")
                .lastname("Vanyechkin")
                .build();
        Instant was = profile.getUpdatedAt();
        ReadProfileDto result = profileService.updateProfile(req);
        assertNotNull(result.getUpdatedAt());
        assertNotEquals(was, result.getUpdatedAt());
        assertEquals(req.getFirstname(), result.getFirstName());
        assertEquals(req.getLastname(), result.getLastName());
    }

    @Test
    public void updateProfileWithErrorTest(){
        UpdateProfileDto req = UpdateProfileDto.builder()
                .id(profile.getId())
                .firstname("Vanya")
                .lastname("Vanyechkin")
                .build();
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> profileService.updateProfile(req));
    }

    @Test
    public void deleteProfile_SuccessTest() {
        profile.setOrders(Collections.singletonList(order));
        UUID profileId = profile.getId();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        profileService.deleteProfile(profileId);
        verify(orderService).deleteOrder(order);
        verify(cartRepository).delete(cart);
        verify(profileRepository).delete(profile);
        assertEquals(Collections.emptyList(), cart.getItemList());
    }

    @Test
    public void deleteProfile_NotFoundTest() {
        UUID profileId = profile.getId();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> profileService.deleteProfile(profileId));
        verify(profileRepository, never()).delete(any());
    }

    @Test
    public void updateProfileRoleTest(){
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.of(profile));
        when(profileRepository.save(eq(profile))).thenAnswer(profile1 -> profile1.getArgument(0));
        ReadProfileDto profileDto = profileService.updateProfileRole(profile.getId(), Role.ADMIN);
        assertEquals(Role.ADMIN, profileDto.getRole());
    }

    @Test
    public void updateProfileRoleWithErrorTest(){
        when(profileRepository.findById(eq(profile.getId()))).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> profileService.updateProfileRole(profile.getId(), Role.ADMIN));
    }

}
