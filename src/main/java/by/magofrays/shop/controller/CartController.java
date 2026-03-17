package by.magofrays.shop.controller;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CartItemDto> addItemIntoCart(
            @RequestBody UUID itemId,
            @AuthenticationPrincipal UserDetails principal) {
        UUID profileId = UUID.fromString(principal.getUsername());
        CartItemDto cartItemDto = cartService.addItemIntoCart(itemId, profileId);
        return ResponseEntity.ok(
                cartItemDto
        );
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<?> removeItemFromCart(
            @RequestBody UUID cartItemId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        UUID profileId = UUID.fromString(principal.getUsername());
        cartService.removeItemFromCart(cartItemId, profileId);
        return ResponseEntity.noContent().build();
    }


}
