package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.CartItem;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.repository.CartItemRepository;
import by.magofrays.shop.repository.ItemRepository;
import by.magofrays.shop.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProfileRepository profileRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CartItemDto addItemIntoCart(UUID itemId, UUID profileId){
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Cart cart = profile.getCart();
        CartItem cartItem = createCartItem(cart, item);
        return modelMapper.map(cartItem, CartItemDto.class);
    }

    public CartItem createCartItem(Cart cart, Item item){
        return cartItemRepository.save(
                CartItem.builder()
                .cart(cart)
                .item(item)
                .build());
    }
}
