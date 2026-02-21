package by.magofrays.shop.service;

import by.magofrays.shop.dto.CreateUpdateProfileDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return profileRepository.findByEmail(email).map(profile ->
                new User(
                        profile.getEmail(),
                        profile.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(profile.getRole().name()))
)                ).orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND)
                );
    }



    @Transactional
    public UserDetails createProfile(CreateUpdateProfileDto createProfileDto){
        Profile profile = modelMapper.map(createProfileDto, Profile.class);
        profile.setRole(Role.CLIENT);
        Cart cart = new Cart();
        profile.setCart(cart);
        profileRepository.save(profile);
        return new User(profile.getEmail(), "",
                Collections.singletonList( new SimpleGrantedAuthority(profile.getRole().name())));
    }

}
