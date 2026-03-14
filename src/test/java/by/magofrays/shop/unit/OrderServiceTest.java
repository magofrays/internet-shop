package by.magofrays.shop.unit;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.entity.*;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.*;
import by.magofrays.shop.repository.*;
import by.magofrays.shop.service.MailService;
import by.magofrays.shop.service.OrderService;
import by.magofrays.shop.service.ReceiptGenerateService;
import by.magofrays.shop.utils.LocalStorageTest;
import lombok.SneakyThrows;
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
    private UUID orderId;
    private OrderDto orderDto;
    private ItemDto itemDto2;
    private ItemDto itemDto1;
    private Order order;

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
        LocalStorageTest lst = new LocalStorageTest();
        profileId = lst.profileId;
        profile = lst.profile;
        profileDto = lst.profileDto;
        item1 = lst.item1;
        item2 = lst.item2;
        cartItem1 = lst.cartItem1;
        cartItem2 = lst.cartItem2;
        cartItemDto1 = lst.cartItemDto1;
        cartItemDto2 = lst.cartItemDto2;
        orderItemDto1 = lst.orderItemDto1;
        orderItemDto2 = lst.orderItemDto2;
        itemDto1 = lst.itemDto1;
        itemDto2 = lst.itemDto2;
        orderItem1 = lst.orderItem1;
        orderItem2 = lst.orderItem2;
        orderId = lst.orderId;
        orderDto = lst.orderDto;
        order = lst.order;
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
        
        when(orderRepository.save(any(Order.class))).thenAnswer(
                invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(orderId);
                    order.setCreatedAt(orderDto.getCreatedAt());
                    order.setUpdatedAt(orderDto.getUpdatedAt());
                    return order;
                }
        );

        String receiptUrl = "save/pdf/receipt";
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn(receiptUrl);
        OrderDto result = orderService.createOrder(items, profileId);

        itemDto1.setQuantity(itemDto1.getQuantity()-1);
        itemDto2.setQuantity(itemDto2.getQuantity()-1);
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
            o.setId(orderId);
            return o;
        });
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn("url");
        doThrow(new MessagingException("SMTP error")).when(mailService).sendEmailWithAttachment(any(), any(), any(), any());

        assertThatThrownBy(() -> orderService.createOrder(items, profileId))
                .isInstanceOf(BusinessException.class);

        verify(orderRepository).save(any());
        verify(receiptGenerateService).createReceipt(any());
        verify(mailService).sendEmailWithAttachment(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createOrder_emptyCart_resultError() {
        List<CartItemDto> items = Collections.emptyList();
        assertThatThrownBy(() -> orderService.createOrder(items, profileId)).isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteOrder_success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        orderService.deleteOrder(orderId);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).delete(order);
        assertThat(order.getItemList()).isEmpty();
    }

    @Test
    void deleteOrder_orderNotFound_throwsException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    @SneakyThrows
    void updateOrder_removeOneItemAndAddNew_success() {
        UUID newItemId = UUID.randomUUID();
        Item newItem = Item.builder()
                .id(newItemId)
                .title("Новый товар")
                .description("Описание нового товара")
                .quantity(5L)
                .price(new BigDecimal(2000))
                .discountPrice(new BigDecimal(1500))
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .id(newItemId)
                .title("Новый товар")
                .description("Описание нового товара")
                .quantity(5L)
                .price(new BigDecimal(2000))
                .discountPrice(new BigDecimal(1500))
                .build();
        CartItemDto existingItemDto = CartItemDto.builder()
                .id(UUID.randomUUID())
                .item(itemDto1)
                .addedAt(Instant.now())
                .build();
        CartItemDto newCartItemDto = CartItemDto.builder()
                .id(UUID.randomUUID())
                .item(newItemDto)
                .addedAt(Instant.now())
                .build();
        List<CartItemDto> updatedItems = Arrays.asList(existingItemDto, newCartItemDto);
        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(orderId)
                .items(updatedItems)
                .build();


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(itemRepository.findById(itemDto1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(newItemId)).thenReturn(Optional.of(newItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn("pdf/receipt/url");
        doNothing().when(mailService).sendEmailWithAttachment(any(), any(), any(), any());

        OrderDto result = orderService.updateOrder(updateOrderDto);

        verify(orderRepository).findById(orderId);

        ArgumentCaptor<List<OrderItem>> removedItemsCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(orderItemRepository).deleteAll(removedItemsCaptor.capture());
        List<OrderItem> removedItems = removedItemsCaptor.getValue();
        assertThat(removedItems).hasSize(1);
        assertThat(removedItems.get(0).getItem().getId()).isEqualTo(item2.getId());

        verify(itemRepository).save(argThat(item ->
                item.getId().equals(newItemId) && item.getQuantity() == 4L
        ));

        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        BigDecimal expectedTotal = item1.getPrice().add(newItem.getPrice()); // 10000 + 2000 = 12000
        BigDecimal expectedDiscount = item1.getDiscountPrice().add(newItem.getDiscountPrice()); // 5000 + 1500 = 6500

        assertThat(capturedOrder.getTotalCost()).isEqualByComparingTo(expectedTotal);
        assertThat(capturedOrder.getDiscountCost()).isEqualByComparingTo(expectedDiscount);
        assertThat(capturedOrder.getItemList()).hasSize(2);
        assertThat(capturedOrder.getUpdatedAt()).isNotNull();

        verify(mailService).sendEmailWithAttachment(
                eq(profile.getEmail()),
                eq("Оформление заказа"),
                eq("Здравствуйте, Ivan Ivanov.\n" +
                        "Ваш заказ был изменен и ожидает оплаты.\n\nС уважением,\nИнтернет-магазин"),
                eq("pdf/receipt/url")
        );
        assertThat(result).isNotNull();
    }

    @Test
    void updateOrder_removeAllItems_throwsException() {
        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(orderId)
                .items(Collections.emptyList())
                .build();


        assertThatThrownBy(() -> orderService.updateOrder(updateOrderDto))
                .isInstanceOf(BusinessException.class);
        verify(orderRepository, never()).findById(orderId);
        verify(orderItemRepository, never()).deleteAll(any());
        verify(itemRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_orderNotFound_throwsException() {
        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(orderId)
                .items(Arrays.asList(
                        CartItemDto.builder().item(itemDto1).build()
                ))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(updateOrderDto))
                .isInstanceOf(BusinessException.class);

        verify(orderRepository).findById(orderId);
        verifyNoInteractions(itemRepository, orderItemRepository, mailService, receiptGenerateService);
    }

    @Test
    void updateOrder_newItemNotFound_throwsException() {
        UUID nonExistentItemId = UUID.randomUUID();
        CartItemDto newItemDto = CartItemDto.builder()
                .id(nonExistentItemId)
                .item(ItemDto.builder().id(nonExistentItemId).build())
                .build();

        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(orderId)
                .items(Arrays.asList(
                        CartItemDto.builder().item(itemDto1).build(),
                        newItemDto
                ))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(itemRepository.findById(itemDto1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(updateOrderDto))
                .isInstanceOf(BusinessException.class);

        verify(orderRepository).findById(orderId);
        verify(itemRepository).findById(nonExistentItemId);

        verify(orderItemRepository, never()).deleteAll(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_newItemOutOfStock_throwsException() {
        UUID newItemId = UUID.randomUUID();
        Item newItem = Item.builder()
                .id(newItemId)
                .title("Новый товар")
                .description("Описание нового товара")
                .quantity(0L)
                .price(new BigDecimal(2000))
                .discountPrice(new BigDecimal(1500))
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .id(newItemId)
                .title("Новый товар")
                .description("Описание нового товара")
                .quantity(5L)
                .price(new BigDecimal(2000))
                .discountPrice(new BigDecimal(1500))
                .build();
        CartItemDto existingItemDto = CartItemDto.builder()
                .id(UUID.randomUUID())
                .item(itemDto1)
                .addedAt(Instant.now())
                .build();
        CartItemDto newCartItemDto = CartItemDto.builder()
                .id(UUID.randomUUID())
                .item(newItemDto)
                .addedAt(Instant.now())
                .build();
        List<CartItemDto> updatedItems = Arrays.asList(existingItemDto, newCartItemDto);
        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(orderId)
                .items(updatedItems)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(itemRepository.findById(itemDto1.getId())).thenReturn(Optional.of(newItem));

        assertThatThrownBy(() -> orderService.updateOrder(updateOrderDto))
                .isInstanceOf(BusinessException.class);

        verify(orderRepository).findById(orderId);
        verify(itemRepository).findById(newItem.getId());
        verify(orderItemRepository, never()).deleteAll(any());
        verify(itemRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @SneakyThrows
    void updateOrder_emailSendingFails_throwsException() {
        CartItemDto existingItemDto = CartItemDto.builder()
                .id(UUID.randomUUID())
                .item(itemDto1)
                .build();

        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(orderId)
                .items(Collections.singletonList(existingItemDto))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(itemRepository.findById(itemDto1.getId())).thenReturn(Optional.of(item1));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(receiptGenerateService.createReceipt(any(OrderDto.class))).thenReturn("receipt/url");
        doThrow(new MessagingException("SMTP error")).when(mailService)
                .sendEmailWithAttachment(any(), any(), any(), any());

        assertThatThrownBy(() -> orderService.updateOrder(updateOrderDto))
                .isInstanceOf(BusinessException.class);

        verify(orderRepository).save(any());
        verify(receiptGenerateService).createReceipt(any());
        verify(mailService).sendEmailWithAttachment(any(), any(), any(), any());
    }
}