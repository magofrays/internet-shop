package by.magofrays.shop.module;

import by.magofrays.shop.configuration.security.JwtUtils;
import by.magofrays.shop.controller.AuthController;
import by.magofrays.shop.dto.CreateProfileDto;
import by.magofrays.shop.dto.LoginDto;
import by.magofrays.shop.dto.LoginResponse;
import by.magofrays.shop.entity.Cart;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/db/test/01-init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @SneakyThrows
    public void signUpTest(){

        CreateProfileDto profileDto = CreateProfileDto.builder()
                .email("fedor@mail.ru")
                .firstName("fedor")
                .lastName("pukin")
                .password("123456")
                .build();
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isOk())
                .andReturn();


        LoginResponse loginResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                LoginResponse.class
        );

        tokenTest(profileDto, loginResponse);
    }

    public void tokenTest(CreateProfileDto profileDto, LoginResponse loginResponse){
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        Optional<Profile> profileOpt = profileRepository.findByEmail(profileDto.getEmail());
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
        assertEquals(Role.CLIENT, Role.valueOf((String) claims.getBody().get("role")));
        Cart cart = profile.getCart();
        assertNotNull(cart);
        assertNotNull(cart.getProfile());
    }

    @Test
    @SneakyThrows
    public void signUpWithErrorTest(){
        CreateProfileDto profileDto = CreateProfileDto.builder()
                .email("matvey@yandex.ru")
                .firstName("matvey")
                .lastName("pukin")
                .password("123456")
                .build();
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @SneakyThrows
    public void signInTest(){
        LoginDto loginDto = LoginDto.builder()
                .email("matvey@yandex.ru")
                .password("user123")
                .build();
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void signInWithErrorTest(){
        LoginDto loginDto = LoginDto.builder()
                .email("matvey@yandex.ru")
                .password("user1234")
                .build();

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void signInWithErrorTest2(){
        LoginDto loginDto = LoginDto.builder()
                .email("matvey2@yandex.ru")
                .password("user123")
                .build();
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void signUpSignInTest(){
        CreateProfileDto profileDto = CreateProfileDto.builder()
                .email("fedor@mail.ru")
                .firstName("fedor")
                .lastName("pukin")
                .password("123456")
                .build();
        LoginDto loginDto = LoginDto.builder()
                .email("fedor@mail.ru")
                .password("123456")
                .build();
        MvcResult signUpResult = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse signUpResponse = objectMapper.readValue(
                signUpResult.getResponse().getContentAsString(),
                LoginResponse.class
        );
        assertNotNull(signUpResponse);
        assertNotNull(signUpResponse.getToken());
        MvcResult signInResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                signInResult.getResponse().getContentAsString(),
                LoginResponse.class
        );

        tokenTest(profileDto, loginResponse);
    }

}
