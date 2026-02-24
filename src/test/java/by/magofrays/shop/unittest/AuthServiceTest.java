package by.magofrays.shop.unittest;


import by.magofrays.shop.configuration.security.JwtUtils;
import by.magofrays.shop.configuration.security.SecurityJwtProperties;
import by.magofrays.shop.dto.CreateUpdateProfileDto;
import by.magofrays.shop.dto.LoginResponse;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.repository.ProfileRepository;
import by.magofrays.shop.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                AuthService.class,
                ModelMapper.class,
                BCryptPasswordEncoder.class,
                SecurityJwtProperties.class,
                JwtUtils.class
        }
)
@TestPropertySource(properties = {
        "security.jwt.expires-hours=24",
        "security.jwt.secret=VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBmb3IgSldUIHNpZ25pbmcgdGhhdCBpcyBsb25nIGVub3VnaCB0byBiZSBzZWN1cmU="
})
public class AuthServiceTest {
    @MockBean
    private ProfileRepository profileRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void createProfileTest(){
        CreateUpdateProfileDto createUpdateProfileDto = CreateUpdateProfileDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@mail.ru")
                .password("123")
                .build();
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .firstName("test")
                .lastName("test")
                .email("test@mail.ru")
                .role(Role.CLIENT)
                .password(passwordEncoder.encode("123"))
                        .build();
        Mockito.when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        UserDetails userDetails = authService.createProfile(createUpdateProfileDto);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        Assertions.assertNotEquals(savedProfile.getPassword(), createUpdateProfileDto.getPassword());
        Assertions.assertNotNull(savedProfile.getCart());
        Assertions.assertNotNull(userDetails, "authService.createProfile() returned null");
        Assertions.assertEquals(createUpdateProfileDto.getEmail(), userDetails.getUsername());
        Assertions.assertEquals(
                Collections.singleton(
                        new SimpleGrantedAuthority(Role.CLIENT.name())),
                userDetails.getAuthorities()
                );
        Assertions.assertEquals("", userDetails.getPassword());
    }

    @Test
    public void createProfileWithErrorTest(){
        CreateUpdateProfileDto createProfileDto = CreateUpdateProfileDto.builder()
                .firstName("test")
                .lastName("test")
                .email("test@mail.ru")
                .password("123")
                .build();
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .firstName("test")
                .lastName("test")
                .email("test@mail.ru")
                .role(Role.CLIENT)
                .cart(Cart.builder()
                        .id(UUID.randomUUID())
                        .build())
                .password(passwordEncoder.encode("123"))
                .build();
        Mockito.when(profileRepository.findByEmail(createProfileDto.getEmail())).thenReturn(Optional.of(profile));
        Assertions.assertThrows(BusinessException.class, () -> authService.createProfile(createProfileDto));
    }

    @Test
    public void loadUserByUsernameTest(){
        String email = "test@mail.ru";
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .firstName("test")
                .lastName("test")
                .email("test@mail.ru")
                .role(Role.CLIENT)
                .cart(Cart.builder()
                        .id(UUID.randomUUID())
                        .build())
                .password(passwordEncoder.encode("123"))
                .build();
        Mockito.when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profile));
        UserDetails userDetails = authService.loadUserByUsername(email);
        Assertions.assertNotNull(userDetails, "authService.createProfile() returned null");
        Assertions.assertEquals(email, userDetails.getUsername());
        Assertions.assertEquals(
                Collections.singleton(
                        new SimpleGrantedAuthority(Role.CLIENT.name())),
                userDetails.getAuthorities()
        );
        Assertions.assertEquals(profile.getPassword(), userDetails.getPassword());
    }

    @Test
    public void loadUserByUsernameWithErrorTest(){
        String email = "test@mail.ru";
        Mockito.when(profileRepository.findByEmail(email)).thenReturn(Optional.empty());
        Assertions.assertThrows(BusinessException.class, () -> authService.loadUserByUsername(email));
    }

    @Test
    public void createLoginResponseTest(){
        UserDetails userDetails = new User(
                "test@mail.ru",
                "",
                Collections.singletonList(
                        new SimpleGrantedAuthority(Role.CLIENT.name())
                )
        );
        LoginResponse loginResponse = authService.createLoginResponse(userDetails);
        Assertions.assertNotNull(loginResponse, "authService.createLoginResponse() returned null");
        Assertions.assertTrue(loginResponse.getExpiresAt().isAfter(Instant.now()));
        Optional<Jws<Claims>> claimsOpt = jwtUtils.parseToken(loginResponse.getToken());
        Assertions.assertTrue(claimsOpt.isPresent(), "expiresAt not in future");
        Jws<Claims> claims = claimsOpt.get();
        Assertions.assertEquals(userDetails.getUsername(), claims.getBody().get("id"));
        Assertions.assertEquals(userDetails.getAuthorities().stream().findFirst().toString(), claims.getBody().get("role"));
    }
}
