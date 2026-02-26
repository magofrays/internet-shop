package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.CartItem;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CartItemMapper;
import by.magofrays.shop.repository.CartItemRepository;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ProfileRepository profileRepository;
    private final ItemRepository itemRepository;

    public List<CartItemDto> getItemsInCart(UUID profileId){
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        return profile.getCart()
                .getItemList().stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemDto addItemIntoCart(UUID itemId, UUID profileId){
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Cart cart = profile.getCart();
        CartItem cartItem = createCartItem(cart, item);
        return cartItemMapper.toDto(cartItem);
    }

    @Transactional
    public CartItemDto removeItemFromCart(UUID cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        cartItemRepository.delete(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    public CartItem createCartItem(Cart cart, Item item){
        return cartItemRepository.save(
                CartItem.builder()
                        .cart(cart)
                        .item(item)
                        .build());
    }




}
