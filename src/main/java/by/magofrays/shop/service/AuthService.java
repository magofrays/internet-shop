package by.magofrays.shop.service;

import by.magofrays.shop.configuration.security.JwtUtils;
import by.magofrays.shop.dto.CreateUpdateProfileDto;
import by.magofrays.shop.dto.LoginResponse;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.mapper.ProfileMapper;
import by.magofrays.shop.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileMapper profileMapper;
    private final JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Trying to find profile by email: {}", email);
        return profileRepository.findByEmail(email).map(profile ->
                new User(
                        profile.getEmail(),
                        profile.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(profile.getRole().name()))
)                ).orElseThrow(
                        () -> new BusinessException(HttpStatus.NOT_FOUND)
                );
    }

    public LoginResponse createLoginResponse(UserDetails details){
        log.info("Creating login response for profile: {}", details.getUsername());
        String token = jwtUtils.createJwt(details);
        Instant expiresAt = jwtUtils.parseToken(token).get().getBody().getExpiration().toInstant();
        return LoginResponse.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }

    @Transactional
    public UserDetails createProfile(CreateUpdateProfileDto createProfileDto){
        log.info("Trying to create profile: {}", createProfileDto.getEmail());
        if(profileRepository.findByEmail(createProfileDto.getEmail()).isPresent()){
            log.error("Email {} already taken to create profile", createProfileDto.getEmail());
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        Profile profile = profileMapper.toEntity(createProfileDto);
        profile.setRole(Role.CLIENT);
        profile.setPassword(passwordEncoder.encode(createProfileDto.getPassword()));
        Cart cart = new Cart();
        profile.setCart(cart);
        profileRepository.save(profile);
        log.info("Created profile: {}", profile.getId());
        return new User(profile.getEmail(), "",
                Collections.singletonList( new SimpleGrantedAuthority(profile.getRole().name())));
    }

}
