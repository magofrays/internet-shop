package by.magofrays.shop.integration;

import by.magofrays.shop.Main;
import by.magofrays.shop.configuration.security.JwtUtils;
import by.magofrays.shop.controller.AuthController;
import by.magofrays.shop.dto.CreateProfileDto;
import by.magofrays.shop.dto.LoginResponse;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.repository.ProfileRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = Main.class)
@Sql(scripts = "/db/test/01-profiles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerTest {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @Transactional
    public void registrationTest(){
        CreateProfileDto profileDto = CreateProfileDto.builder()
                .email("fedor@mail.ru")
                .firstName("fedor")
                .lastName("pukin")
                .password("123456")
                .build();
        LoginResponse loginResponse = authController.signUp(profileDto);
        Optional<Profile> profileOpt = profileRepository.findByEmail("fedor@mail.ru");
        assertTrue(profileOpt.isPresent());
        Profile profile = profileOpt.get();
        assertEquals(profileDto.getEmail(), profile.getEmail());
        assertEquals(profileDto.getFirstName(), profile.getFirstName());
        assertEquals(profileDto.getLastName(), profile.getLastName());
        assertNotEquals(profileDto.getPassword(), profile.getPassword());
        assertEquals(Role.CLIENT, profile.getRole());
        Optional<Jws<Claims>> claimsOpt = jwtUtils.parseToken(loginResponse.getToken());
        assertTrue(claimsOpt.isPresent());
        Jws<Claims> claims = claimsOpt.get();
        assertEquals(profileDto.getEmail(), claims.getBody().get("id"));
        assertEquals(Role.CLIENT, Role.valueOf((String) claims.getBody().get("role")));
        Cart cart = profile.getCart();
        assertNotNull(cart);
        assertNotNull(cart.getProfile());
    }
}
;