package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.UpdateOrderDto;
import by.magofrays.shop.dto.UpdateOrderStatus;
import by.magofrays.shop.entity.*;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.OrderMapper;
import by.magofrays.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

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
        if(items.isEmpty()){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Can't create order with empty items!");
        }
        Profile profile = profileRepository.findById(profileId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "Profile not found!")
        );
        Order order = Order.builder()
                .createdBy(profile)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .currency("RUB")
                .build();
        BigDecimal price = new BigDecimal(0);
        BigDecimal discountPrice = new BigDecimal(0);
        for(CartItemDto itemDto : items){
            CartItem cartItem = cartItemRepository.findById(itemDto.getId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found in cart!"));
            Item item = cartItem.getItem();
            if(item.getQuantity() == 0){
                throw new BusinessException(HttpStatus.BAD_REQUEST, "No such item in shop!");
            }
            item.setQuantity(item.getQuantity()-1);
            itemRepository.save(item);
            OrderItem orderItem = createOrderItem(order, item);
            price = price.add(orderItem.getCost());
            discountPrice = discountPrice.add(orderItem.getDiscountCost());
            order.getItemList().add(orderItem);
        }
        order.setTotalCost(price);
        order.setDiscountCost(discountPrice);
        order = orderRepository.save(order);
        log.info("Created order {} for profile {}", order.getId(), profileId);
        OrderDto orderDto = orderMapper.toDto(order);
        createReceipt(orderDto, profile.getEmail(),
                String.format("Здравствуйте, %s %s.\nВаш заказ ожидает оплаты.",
                        profile.getFirstName(), profile.getLastName()));
        return orderDto;
    }

    private void createReceipt(OrderDto orderDto, String email, String message){

        String receiptUrl = receiptGenerateService.createReceipt(orderDto);
        try{
            mailService.sendEmailWithAttachment(email,
                    "Оформление заказа",
                    message + "\n\nС уважением,\nИнтернет-магазин",
                    receiptUrl);
        } catch (MessagingException messagingException){
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't send email!");
        }
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
    public OrderDto getOrderById(UUID id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found!"));
        return orderMapper.toDto(order);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ) // system changes
    public OrderDto updateOrderStatus(UpdateOrderStatus orderStatus){
        Order order = orderRepository.findById(orderStatus.getOrderId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found!"));
        order.setOrderStatus(orderStatus.getOrderStatus());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    public void deleteOrder(Order order){
        order.getItemList().clear();
        orderRepository.delete(order);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteOrder(UUID orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found!"));
        order.getItemList().clear();
        orderRepository.delete(order);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ) // client changes
    public OrderDto updateOrder(UpdateOrderDto orderDto) {
        if(orderDto.getItems().isEmpty()){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Can't update order request has empty items!");
        }
        Order order = orderRepository.findById(orderDto.getOrderId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found!"));
        Profile profile = order.getCreatedBy();
        List<CartItemDto> cartItems = orderDto.getItems();
        List<OrderItem> removalItems = new ArrayList<>();
        Set<CartItemDto> alreadyExist = new HashSet<>();
        BigDecimal cost = new BigDecimal(0);
        BigDecimal discountCost = new BigDecimal(0);
        for (OrderItem orderItem : order.getItemList()) {
            boolean exists = false;
            for (CartItemDto cartItem : cartItems)
                if (!alreadyExist.contains(cartItem) && orderItem.getItem().getId().equals(cartItem.getItem().getId())) {
                    exists = true;
                    cost = cost.add(orderItem.getCost());
                    discountCost = discountCost.add(orderItem.getDiscountCost());
                    alreadyExist.add(cartItem);
                }
            if(!exists){
                removalItems.add(orderItem);
            }
        }

        for (CartItemDto addItem : cartItems){
            if(alreadyExist.contains(addItem)){
               continue;
            }
            Item item = itemRepository.findById(addItem.getItem().getId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found!"));
            if(item.getQuantity() == 0){
                throw new BusinessException(HttpStatus.BAD_REQUEST, "No such item in shop!");
            }
            item.setQuantity(item.getQuantity()-1);
            itemRepository.save(item);
            OrderItem orderItem = createOrderItem(order, item);
            cost = cost.add(orderItem.getCost());
            discountCost = discountCost.add(orderItem.getDiscountCost());
            order.getItemList().add(orderItem);
        }
        order.setTotalCost(cost);
        order.setDiscountCost(discountCost);
        order.setUpdatedAt(Instant.now());
        order.getItemList().removeAll(removalItems);
        orderItemRepository.deleteAll(removalItems);
        orderRepository.save(order);
        OrderDto result = orderMapper.toDto(order);
        createReceipt(result, profile.getEmail(),
                String.format("Здравствуйте, %s %s.\nВаш заказ был изменен и ожидает оплаты.",
                        profile.getFirstName(), profile.getLastName()));
        return result;
    }
}
