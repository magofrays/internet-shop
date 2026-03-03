package by.magofrays.shop.unittest;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.entity.*;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.*;
import by.magofrays.shop.repository.*;
import by.magofrays.shop.service.MailService;
import by.magofrays.shop.service.OrderService;
import by.magofrays.shop.service.ReceiptGenerateService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                OrderMapperImpl.class,
                OrderItemMapperImpl.class,
                ProfileMapperImpl.class,
                ItemMapperImpl.class
        }
)
class OrderServiceTest {

    @Autowired
    private OrderMapper orderMapper;

    @MockBean
    private MailService mailService;

    @MockBean
    private ReceiptGenerateService receiptGenerateService;

    @MockBean
    private OrderItemRepository orderItemRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CartItemRepository cartItemRepository;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Captor
    private ArgumentCaptor<OrderItem> orderItemCaptor;

    private OrderService orderService;

    private UUID profileId;
    private Profile profile;
    private ReadProfileDto profileDto;
    private CartItem cartItem1, cartItem2;
    private CartItemDto cartItemDto1, cartItemDto2;
    private Item item1, item2;
    private OrderItemDto orderItemDto1;
    private OrderItem orderItem2;
    private OrderItemDto orderItemDto2;
    private OrderItem orderItem1;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderItemRepository,
                orderRepository,
                cartItemRepository,
                profileRepository,
                orderMapper,
                mailService,
                receiptGenerateService,
                itemRepository
        );
        Instant createdAt = Instant.now();
        profileId = UUID.randomUUID();
        profile = Profile.builder()
                .id(profileId)
                .email("test@email.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
        profileDto = ReadProfileDto.builder()
                .id(profileId)
                .email("test@email.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();


        item1 = Item.builder()
                .id(UUID.randomUUID())
                .title("Наушники GIGABYTE")
                .description("Крутые наушники за высокую цену")
                .quantity(10L)
                .discountPrice(new BigDecimal(5000))
                .price(new BigDecimal(10000))
                .build();

        item2 = Item.builder()
                .id(UUID.randomUUID())
                .title("Очень длинный шнур для блока питания")
                .description("500 метров шнура хватит, чтобы подключить компьютер в тайге")
                .quantity(5L)
                .discountPrice(new BigDecimal(500))
                .price(new BigDecimal(1000))
                .build();

        cartItem1 = CartItem.builder()
                .id(UUID.randomUUID())
                .item(item1)
                .addedAt(Instant.now())
                .build();

        cartItem2 = CartItem.builder()
                .id(UUID.randomUUID())
                .item(item2)
                .addedAt(Instant.now())
                .build();

        cartItemDto1 = CartItemDto.builder()
                .id(cartItem1.getId())
                .item(null)
                .build();

        cartItemDto2 = CartItemDto.builder()
                .id(cartItem2.getId())
                .item(null)
                .build();
        orderItemDto1 = OrderItemDto.builder()
                .id(UUID.randomUUID())
                .item(
                        ItemDto.builder()
                                .id(item1.getId())
                                .title("Наушники GIGABYTE")
                                .description("Крутые наушники за высокую цену")
                                .quantity(9L)
                                .discountPrice(new BigDecimal(5000))
                                .price(new BigDecimal(10000))
                                .build()
                )
                .cost(new BigDecimal(10000))
                .discountCost(new BigDecimal(5000))
                .createdAt(Instant.now())
                .build();

        orderItemDto2 = OrderItemDto.builder()
                .id(UUID.randomUUID())
                .item(
                        ItemDto.builder()
                                .id(item2.getId())
                                .title("Очень длинный шнур для блока питания")
                                .description("500 метров шнура хватит, чтобы подключить компьютер в тайге")
                                .quantity(4L)
                                .discountPrice(new BigDecimal(500))
                                .price(new BigDecimal(1000))
                                .build()
                )
                .cost(new BigDecimal(1000))
                .discountCost(new BigDecimal(500))
                .createdAt(Instant.now())
                .build();

        orderItem1 = OrderItem.builder()
                .id(orderItemDto1.getId())
                .item(
                        item1
                )
                .cost(new BigDecimal(10000))
                .discountCost(new BigDecimal(5000))
                .createdAt(orderItemDto1.getCreatedAt())
                .build();

        orderItem2 = OrderItem.builder()
                .id(orderItemDto2.getId())
                .item(
                       item2
                )
                .cost(new BigDecimal(1000))
                .discountCost(new BigDecimal(500))
                .createdAt(orderItemDto2.getCreatedAt())
                .build();
    }

    @Test
    @SneakyThrows
    void createOrder_success() {
        List<CartItemDto> items = Arrays.asList(cartItemDto1, cartItemDto2);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(cartItemRepository.findById(cartItemDto1.getId())).thenReturn(Optional.of(cartItem1));
        when(cartItemRepository.findById(cartItemDto2.getId())).thenReturn(Optional.of(cartItem2));
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> {
                    OrderItem orderItem = invocation.getArgument(0);
                    Item item = orderItem.getItem();
                    if (item.getId().equals(item1.getId())) {
                        orderItem.setId(orderItem1.getId());
                        orderItem.setCreatedAt(orderItem1.getCreatedAt());
                    } else if (item.getId().equals(item2.getId())) {
                        orderItem.setId(orderItem2.getId());
                        orderItem.setCreatedAt(orderItem2.getCreatedAt());
                    } else {
                        throw new AssertionError("Unexpected item saved: " + item.getId() +
                                " with title: " + item.getTitle());
                    }


                    return orderItem;
                });

        UUID orderId = UUID.randomUUID();
        when(orderRepository.save(any(Order.class))).thenAnswer(
                invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(orderId);
                    return order;
                }
        );

        String receiptUrl = "save/pdf/receipt";
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn(receiptUrl);


        OrderDto result = orderService.createOrder(items, profileId);


        verify(profileRepository).findById(profileId);

        verify(cartItemRepository).findById(cartItemDto1.getId());
        verify(cartItemRepository).findById(cartItemDto2.getId());

        verify(itemRepository, times(2)).save(itemCaptor.capture());
        List<Item> savedItems = itemCaptor.getAllValues();
        assertThat(savedItems).hasSize(2);
        assertThat(savedItems.get(0).getQuantity()).isEqualTo(9L);
        assertThat(savedItems.get(1).getQuantity()).isEqualTo(4L);

        verify(orderItemRepository, times(2)).save(orderItemCaptor.capture());
        List<OrderItem> savedOrderItems = orderItemCaptor.getAllValues();
        assertThat(savedOrderItems).hasSize(2);
        assertThat(savedOrderItems.get(0).getItem()).isEqualTo(item1);
        assertThat(savedOrderItems.get(0).getCost()).isEqualTo(item1.getPrice());
        assertThat(savedOrderItems.get(0).getDiscountCost()).isEqualTo(item1.getDiscountPrice());
        assertThat(savedOrderItems.get(1).getItem()).isEqualTo(item2);

        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getCreatedBy()).isEqualTo(profile);
        assertThat(capturedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(capturedOrder.getCurrency()).isEqualTo("RUB");
        assertThat(capturedOrder.getTotalCost()).isEqualByComparingTo("11000");
        assertThat(capturedOrder.getDiscountCost()).isEqualByComparingTo("5500");
        assertThat(capturedOrder.getItemList()).hasSize(2);

        verify(receiptGenerateService).createReceipt(any(OrderDto.class));

        verify(mailService).sendEmailWithAttachment(
                eq(profile.getEmail()),
                eq("Оформление заказа"),
                contains("Ivan Ivanov"),
                eq(receiptUrl)
        );
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .totalCost(new BigDecimal(11000))
                .createdBy(profileDto)
                .discountCost(new BigDecimal(5500))
                .itemList(
                        Arrays.asList(orderItemDto1, orderItemDto2)
                )
                .currency("RUB")
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(orderDto);
    }

    @Test
    void createOrder_profileNotFound_throwsException() {
        List<CartItemDto> items = Collections.singletonList(cartItemDto1);
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(items, profileId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

        verify(profileRepository).findById(profileId);
        verifyNoInteractions(cartItemRepository, itemRepository, orderItemRepository, orderRepository,
                receiptGenerateService, mailService);
    }

    @Test
    void createOrder_cartItemNotFound_throwsException() {
        List<CartItemDto> items = Collections.singletonList(cartItemDto1);
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(cartItemRepository.findById(cartItemDto1.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(items, profileId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

        verify(cartItemRepository).findById(cartItemDto1.getId());
        verifyNoMoreInteractions(itemRepository, orderItemRepository, orderRepository,
                receiptGenerateService, mailService);
    }

    @Test
    void createOrder_itemOutOfStock_throwsException() {

        item1.setQuantity(0L);
        List<CartItemDto> items = Collections.singletonList(cartItemDto1);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(cartItemRepository.findById(cartItemDto1.getId())).thenReturn(Optional.of(cartItem1));

        assertThatThrownBy(() -> orderService.createOrder(items, profileId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);


        verify(itemRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_emailSendingFails_throwsInternalServerError() throws MessagingException {
        List<CartItemDto> items = Collections.singletonList(cartItemDto1);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(cartItemRepository.findById(cartItemDto1.getId())).thenReturn(Optional.of(cartItem1));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn("url");
        doThrow(new MessagingException("SMTP error")).when(mailService).sendEmailWithAttachment(any(), any(), any(), any());

        assertThatThrownBy(() -> orderService.createOrder(items, profileId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);

        verify(orderRepository).save(any());
        verify(receiptGenerateService).createReceipt(any());
        verify(mailService).sendEmailWithAttachment(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createOrder_emptyCart_createsOrderWithZeroTotals() {
        List<CartItemDto> items = Collections.emptyList();

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        Order savedOrder = Order.builder()
                .id(UUID.randomUUID())
                .createdBy(profile)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .currency("RUB")
                .totalCost(BigDecimal.ZERO)
                .discountCost(BigDecimal.ZERO)
                .itemList(Collections.emptyList())
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn("receiptUrl");

        OrderDto result = orderService.createOrder(items, profileId);

        verify(orderRepository).save(orderCaptor.capture());
        Order captured = orderCaptor.getValue();
        assertThat(captured.getTotalCost()).isEqualByComparingTo("0");
        assertThat(captured.getDiscountCost()).isEqualByComparingTo("0");
        assertThat(captured.getItemList()).isEmpty();

        verifyNoInteractions(cartItemRepository, itemRepository, orderItemRepository);
        verify(mailService).sendEmailWithAttachment(any(), any(), any(), any());
    }
}