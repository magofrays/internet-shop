package by.magofrays.shop.controller;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.UpdateOrderDto;
import by.magofrays.shop.dto.UpdateOrderStatus;
import by.magofrays.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody @Validated List<CartItemDto> items,
            @AuthenticationPrincipal UserDetails principal){
        UUID profileId = UUID.fromString(principal.getUsername());
        return ResponseEntity.ok(
                orderService.createOrder(items, profileId)
        );
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID orderId){
        return ResponseEntity.ok(
                orderService.getOrderById(orderId)
        );
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @PutMapping
    public ResponseEntity<OrderDto> updateOrder(@RequestBody @Validated UpdateOrderDto orderDto){
        return ResponseEntity.ok(
                orderService.updateOrder(orderDto)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@RequestBody @Validated UpdateOrderStatus orderStatus){
        return ResponseEntity.ok(
                orderService.updateOrderStatus(orderStatus)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

}
