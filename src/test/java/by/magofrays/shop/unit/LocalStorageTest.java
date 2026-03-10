package by.magofrays.shop.unit;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.entity.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class LocalStorageTest {
    Instant createdAt = Instant.now();
    public UUID profileId = UUID.randomUUID();
    public Profile profile = Profile.builder()
            .id(profileId)
                .email("test@email.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    public ReadProfileDto profileDto = ReadProfileDto.builder()
            .id(profileId)
                .email("test@email.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();


    public Item item1 = Item.builder()
            .id(UUID.randomUUID())
            .title("Наушники GIGABYTE")
            .description("Крутые наушники за высокую цену")
            .quantity(10L)
            .discountPrice(new BigDecimal(5000))
            .price(new BigDecimal(10000))
            .build();

    public Item item2 = Item.builder()
            .id(UUID.randomUUID())
            .title("Очень длинный шнур для блока питания")
            .description("500 метров шнура хватит, чтобы подключить компьютер в тайге")
            .quantity(5L)
            .discountPrice(new BigDecimal(500))
            .price(new BigDecimal(1000))
            .build();

    public ItemDto itemDto1 = ItemDto.builder()
            .id(item1.getId())
            .title("Наушники GIGABYTE")
            .description("Крутые наушники за высокую цену")
            .quantity(10L)
            .discountPrice(new BigDecimal(5000))
            .price(new BigDecimal(10000))
            .build();

    public ItemDto itemDto2 = ItemDto.builder()
            .id(item2.getId())
            .title("Очень длинный шнур для блока питания")
            .description("500 метров шнура хватит, чтобы подключить компьютер в тайге")
            .quantity(5L)
            .discountPrice(new BigDecimal(500))
            .price(new BigDecimal(1000))
            .build();

    public CartItem cartItem1 = CartItem.builder()
            .id(UUID.randomUUID())
            .item(item1)
                .addedAt(Instant.now())
            .build();

    public CartItem cartItem2 = CartItem.builder()
            .id(UUID.randomUUID())
            .item(item2)
                .addedAt(Instant.now())
            .build();

    public CartItemDto cartItemDto1 = CartItemDto.builder()
            .id(cartItem1.getId())
            .item(itemDto1)
            .addedAt(cartItem1.getAddedAt())
                .build();

    public CartItemDto cartItemDto2 = CartItemDto.builder()
            .id(cartItem2.getId())
            .item(itemDto2)
            .addedAt(cartItem2.getAddedAt())
                .build();
    public OrderItemDto orderItemDto1 = OrderItemDto.builder()
            .id(UUID.randomUUID())
            .item(itemDto1)
            .cost(new BigDecimal(10000))
            .discountCost(new BigDecimal(5000))
            .createdAt(Instant.now())
            .build();

    public OrderItemDto orderItemDto2 = OrderItemDto.builder()
            .id(UUID.randomUUID())
            .item(itemDto2)
            .cost(new BigDecimal(1000))
            .discountCost(new BigDecimal(500))
            .createdAt(Instant.now())
            .build();

    public OrderItem orderItem1 = OrderItem.builder()
            .id(orderItemDto1.getId())
            .item(
            item1
            )
                .cost(new BigDecimal(10000))
            .discountCost(new BigDecimal(5000))
            .createdAt(orderItemDto1.getCreatedAt())
            .build();

    public OrderItem orderItem2 = OrderItem.builder()
            .id(orderItemDto2.getId())
            .item(
            item2
            )
                .cost(new BigDecimal(1000))
            .discountCost(new BigDecimal(500))
            .createdAt(orderItemDto2.getCreatedAt())
            .build();

    public UUID orderId = UUID.randomUUID();
    public OrderDto orderDto = OrderDto.builder()
            .id(orderId)
                .totalCost(new BigDecimal(11000))
            .createdBy(profileDto)
                .discountCost(new BigDecimal(5500))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .itemList(
            Arrays.asList(orderItemDto1, orderItemDto2)
                )
                        .currency("RUB")
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();
    public Cart cart = Cart.builder()
            .id(UUID.randomUUID())
            .profile(profile)
            .itemList(new ArrayList<>(Arrays.asList(
                    cartItem1,
                    cartItem2
            )))
            .build();
    {
        profile.setCart(cart);
    }
    public CartDto cartDto = CartDto.builder()
            .id(cart.getId())
            .profileId(profileId)
            .itemList(Arrays.asList(
                    cartItemDto1,
                    cartItemDto2
            ))
            .build();

    public Order order = Order.builder()
            .id(orderId)
            .createdBy(profile)
            .currency("RUB")
            .orderStatus(OrderStatus.PENDING_PAYMENT)
            .discountCost(orderDto.getDiscountCost())
            .totalCost(orderDto.getTotalCost())
            .itemList(new ArrayList<>(Arrays.asList(
                    orderItem1,
                    orderItem2)
            ))
            .createdAt(orderDto.getCreatedAt())
            .updatedAt(orderDto.getUpdatedAt())
            .build();
}
