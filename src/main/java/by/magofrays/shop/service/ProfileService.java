package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CartMapper;
import by.magofrays.shop.mapper.ProfileMapper;
import by.magofrays.shop.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final CartMapper cartMapper;

    public List<OrderDto> getOrders(UUID profileId){
        return null;
    }

    public CartDto getCart(UUID profileId){
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Cart cart = profile.getCart();
        return cartMapper.toDto(cart);
    }

    public ReadProfileDto getProfileInfo(UUID profileId){
        return profileMapper.toDto(profileRepository.findById(profileId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND)));
    }
}
