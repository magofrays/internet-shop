package by.magofrays.shop.service;

import by.magofrays.shop.dto.CartDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.dto.UpdateProfileDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.CartMapper;
import by.magofrays.shop.mapper.OrderMapper;
import by.magofrays.shop.mapper.ProfileMapper;
import by.magofrays.shop.repository.OrderRepository;
import by.magofrays.shop.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    public List<OrderDto> getOrders(UUID profileId){
        if(!profileRepository.existsProfileById(profileId)){
            throw new BusinessException(HttpStatus.NOT_FOUND);
        }
        return orderRepository.findByCreatedBy_Id(profileId).stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    public CartDto getCart(UUID profileId){
        log.info("Getting cart for profile: {}", profileId);
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        Cart cart = profile.getCart();
        return cartMapper.toDto(cart);
    }

    public ReadProfileDto getProfileInfo(UUID profileId){
        log.info("Getting profile info for profile: {}", profileId);
        return profileMapper.toDto(profileRepository.findById(profileId).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND)));
    }

    public ReadProfileDto updateProfile(UpdateProfileDto updateProfileDto){
        Profile profile = profileRepository.findById(updateProfileDto.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        profile.setFirstName(updateProfileDto.getFirstname());
        profile.setLastName(updateProfileDto.getLastname());
        profileRepository.save(profile);
        return profileMapper.toDto(profile);
    }

    public void deleteProfile(UUID profileId){
        log.info("Deleting profile: {}", profileId);
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND));
        profileRepository.delete(profile);
    }


}
