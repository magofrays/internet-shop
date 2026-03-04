package by.magofrays.shop.unittest;

import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.entity.*;
import by.magofrays.shop.mapper.*;
import by.magofrays.shop.repository.OrderRepository;
import by.magofrays.shop.repository.ProfileRepository;
import by.magofrays.shop.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
    ProfileRepository profileRepository;
    Profile profile;
    Cart cart;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    public void init(){
        LocalStorageTest lst = new LocalStorageTest();
        profile = lst.profile;
        cart = lst.cart;
        order = lst.order;
        orderDto = lst.orderDto;
    }

    @Test
    public void getOrdersTest(){
        when(profileRepository.existsProfileById(eq(profile.getId()))).thenReturn(true);
        when(orderRepository.findByCreatedBy_Id(eq(profile.getId()))).thenReturn(Collections.singletonList(order));
        List<OrderDto> orderDtos = profileService.getOrders(profile.getId());
        assertEquals(orderDto, orderDtos.get(0));
    }

}
