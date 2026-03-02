package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.entity.*;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.OrderMapper;
import by.magofrays.shop.repository.*;
import liquibase.pro.packaged.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProfileRepository profileRepository;
    private final OrderMapper orderMapper;
    private final MailService mailService;
    private final ReceiptGenerateService receiptGenerateService;
    private final ItemRepository itemRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto createOrder(List<CartItemDto> items, UUID profileId){
        Profile profile = profileRepository.findById(profileId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND)
        );
        Order order = Order.builder()
                .createdBy(profile)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .currency("RUB")
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal price = new BigDecimal(0);
        BigDecimal discountPrice = new BigDecimal(0);
        for(CartItemDto itemDto : items){
            CartItem cartItem = cartItemRepository.findById(itemDto.getId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
            Item item = cartItem.getItem();
            if(item.getQuantity() == 0){
                throw new BusinessException(HttpStatus.BAD_REQUEST);
            }
            item.setQuantity(item.getQuantity()-1);
            itemRepository.save(item);
            OrderItem orderItem = createOrderItem(order, item);
            price = price.add(orderItem.getCost());
            discountPrice = discountPrice.add(orderItem.getDiscountCost());
            orderItems.add(
                    orderItem
            );
        }
        order.setTotalCost(price);
        order.setDiscountCost(discountPrice);
        order.setItemList(orderItems);
        order = orderRepository.save(order);
        log.info("Created order {} for profile {}", order.getId(), profileId);
        OrderDto orderDto = orderMapper.toDto(order);
        String receiptUrl = receiptGenerateService.createReceipt(orderDto);
        try{
        mailService.sendEmailWithAttachment(profile.getEmail(),
                "Оформление заказа",
                "Здравствуйте, " + profile.getFirstName() + " " + profile.getLastName() + ". \n Ваш заказ ожидает оплаты.",
                receiptUrl);
        } catch (MessagingException messagingException){
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return orderDto;
    }

    public OrderItem createOrderItem(Order order, Item item){
        return orderItemRepository.save(OrderItem.builder()
                .order(order)
                .item(item)
                .cost(item.getPrice())
                .discountCost(item.getDiscountPrice())
                .build());
    }


    @Transactional
    public OrderDto updateOrder(){
        return null;
    }
}
