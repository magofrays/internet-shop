package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.entity.Order;
import by.magofrays.shop.entity.OrderItem;
import by.magofrays.shop.repository.OrderItemRepository;
import by.magofrays.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderDto createOrder(List<CartItemDto> items, UUID profileId){
        return null;
    }

    public OrderItem createOrderItem(Order order, Item item){
        return orderItemRepository.save(
                OrderItem.builder()
                        .order(order)
                        .item(item)
                        .build()
        );
    }

    @Transactional
    public OrderDto updateOrder(){
        return null;
    }
}
