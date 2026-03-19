package by.magofrays.shop.utils;

import by.magofrays.shop.dto.*;
import by.magofrays.shop.entity.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class LocalStorageTest {
    final Instant createdAt = Instant.now();
    public final UUID profileId = UUID.randomUUID();
    public final Profile profile = Profile.builder()
            .id(profileId)
                .email("test@email.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    public final ReadProfileDto profileDto = ReadProfileDto.builder()
            .id(profileId)
                .email("test@email.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.CLIENT)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();


    public final Item item1 = Item.builder()
            .id(UUID.randomUUID())
            .title("Наушники GIGABYTE")
            .description("Крутые наушники за высокую цену")
            .quantity(10L)
            .discountPrice(new BigDecimal(5000))
            .price(new BigDecimal(10000))
            .build();

    public final Item item2 = Item.builder()
            .id(UUID.randomUUID())
            .title("Очень длинный шнур для блока питания")
            .description("500 метров шнура хватит, чтобы подключить компьютер в тайге")
            .quantity(5L)
            .discountPrice(new BigDecimal(500))
            .price(new BigDecimal(1000))
            .build();

    public final ItemDto itemDto1 = ItemDto.builder()
            .id(item1.getId())
            .title("Наушники GIGABYTE")
            .description("Крутые наушники за высокую цену")
            .quantity(10L)
            .discountPrice(new BigDecimal(5000))
            .price(new BigDecimal(10000))
            .build();

    public final ItemDto itemDto2 = ItemDto.builder()
            .id(item2.getId())
            .title("Очень длинный шнур для блока питания")
            .description("500 метров шнура хватит, чтобы подключить компьютер в тайге")
            .quantity(5L)
            .discountPrice(new BigDecimal(500))
            .price(new BigDecimal(1000))
            .build();

    public final CartItem cartItem1 = CartItem.builder()
            .id(UUID.randomUUID())
            .item(item1)
                .addedAt(Instant.now())
            .build();

    public final CartItem cartItem2 = CartItem.builder()
            .id(UUID.randomUUID())
            .item(item2)
                .addedAt(Instant.now())
            .build();

    public final CartItemDto cartItemDto1 = CartItemDto.builder()
            .id(cartItem1.getId())
            .item(itemDto1)
            .addedAt(cartItem1.getAddedAt())
                .build();

    public final CartItemDto cartItemDto2 = CartItemDto.builder()
            .id(cartItem2.getId())
            .item(itemDto2)
            .addedAt(cartItem2.getAddedAt())
                .build();
    public final OrderItemDto orderItemDto1 = OrderItemDto.builder()
            .id(UUID.randomUUID())
            .item(itemDto1)
            .cost(new BigDecimal(10000))
            .discountCost(new BigDecimal(5000))
            .createdAt(Instant.now())
            .build();

    public final OrderItemDto orderItemDto2 = OrderItemDto.builder()
            .id(UUID.randomUUID())
            .item(itemDto2)
            .cost(new BigDecimal(1000))
            .discountCost(new BigDecimal(500))
            .createdAt(Instant.now())
            .build();

    public final OrderItem orderItem1 = OrderItem.builder()
            .id(orderItemDto1.getId())
            .item(
            item1
            )
                .cost(new BigDecimal(10000))
            .discountCost(new BigDecimal(5000))
            .createdAt(orderItemDto1.getCreatedAt())
            .build();

    public final OrderItem orderItem2 = OrderItem.builder()
            .id(orderItemDto2.getId())
            .item(
            item2
            )
                .cost(new BigDecimal(1000))
            .discountCost(new BigDecimal(500))
            .createdAt(orderItemDto2.getCreatedAt())
            .build();

    public final UUID orderId = UUID.randomUUID();
    public final OrderDto orderDto = OrderDto.builder()
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
    public final Cart cart = Cart.builder()
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
    public final CartDto cartDto = CartDto.builder()
            .id(cart.getId())
            .profileId(profileId)
            .itemList(Arrays.asList(
                    cartItemDto1,
                    cartItemDto2
            ))
            .build();

    public final Order order = Order.builder()
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
    public final Category rootCategory = Category.builder()
            .id(UUID.randomUUID())
            .title("Электротехника")
            .description("Бытовая техника и электроника")
            .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
            .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
            .build();
    public final Category category1 = Category.builder()
            .id(UUID.randomUUID())
            .title("Электроника")
            .parentCatalogue(rootCategory)
            .description("Различная электроника")
            .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
            .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
            .build();

    public final Category categoryKid1 = Category.builder()
            .id(UUID.randomUUID())
            .title("Компьютерная техника")
            .description("Аксессуары и компьютерная техника")
            .parentCatalogue(category1)
            .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
            .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
            .build();
    {
        categoryKid1.getItemList().addAll(Arrays.asList(item1, item2));
        category1.getCategoryList().add(categoryKid1);
        rootCategory.getCategoryList().add(category1);
    }

    public final CategoryDto rootCategoryDto = CategoryDto.builder()
            .id(rootCategory.getId())
            .description(rootCategory.getDescription())
            .title(rootCategory.getTitle())
            .build();

    public final CategoryDto category1Dto = CategoryDto.builder()
            .id(category1.getId())
            .title(category1.getTitle())
            .description(category1.getDescription())
            .build();
    public final CategoryDto categoryKid1Dto = CategoryDto.builder()
            .id(categoryKid1.getId())
            .title(categoryKid1.getTitle())
            .description(categoryKid1.getDescription())
            .build();

    public final FullCategoryDto fullRootCategoryDto = FullCategoryDto.builder()
            .id(rootCategory.getId())
            .title(rootCategory.getTitle())
            .description(rootCategory.getDescription())
            .createdAt(rootCategory.getCreatedAt())
            .updatedAt(rootCategory.getUpdatedAt())
            .build();

    public final FullCategoryDto fullCategory1Dto = FullCategoryDto.builder()
            .id(category1.getId())
            .title(category1.getTitle())
            .description(category1Dto.getDescription())
            .createdAt(category1.getCreatedAt())
            .updatedAt(category1.getUpdatedAt())
            .build();

    public final FullCategoryDto fullCategoryKid1Dto = FullCategoryDto.builder()
            .id(categoryKid1.getId())
            .title(categoryKid1.getTitle())
            .description(categoryKid1.getDescription())
            .createdAt(categoryKid1.getCreatedAt())
            .updatedAt(categoryKid1.getUpdatedAt())
            .build();

    {
        fullCategoryKid1Dto.getItemList().addAll(Arrays.asList(itemDto1, itemDto2));
        fullCategory1Dto.getCategoryList().add(fullCategoryKid1Dto);
        fullRootCategoryDto.getCategoryList().add(fullCategory1Dto);
    }
}
