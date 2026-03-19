package by.magofrays.shop.module;

import by.magofrays.shop.controller.ProfileController;
import by.magofrays.shop.dto.CartDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.dto.UpdateProfileDto;
import by.magofrays.shop.entity.Profile;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.repository.OrderRepository;
import by.magofrays.shop.repository.ProfileRepository;
import by.magofrays.shop.utils.LocalStorageTest;
import by.magofrays.shop.utils.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProfileControllerTest {

    @Autowired
    ProfileController profileController;

    LocalStorageTest lst;

    @Autowired
    TokenGenerator tokenGenerator;

    @MockBean
    ProfileRepository profileRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderRepository orderRepository;

    @BeforeEach
    public void init(){
           lst = new LocalStorageTest();
    }

    @Test
    @SneakyThrows
    public void getProfileInfoTest(){
        when(profileRepository.findById(any(UUID.class))).thenReturn(Optional.of(lst.profile));
        MvcResult mvcResult = mockMvc.perform(get("/api/profile")
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ReadProfileDto profileDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReadProfileDto.class);
        assertEquals(lst.profileDto, profileDto);
    }


    @Test
    @SneakyThrows
    public void getForbiddenError(){
        mockMvc.perform(get("/api/profile")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/profile/cart")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/profile/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/profile/all")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/profile/" + lst.profileId)
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getCartTest(){
        when(profileRepository.findById(any(UUID.class))).thenReturn(Optional.of(lst.profile));
        MvcResult mvcResult = mockMvc.perform(get("/api/profile/cart")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartDto cartDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CartDto.class);
        assertEquals(lst.cartDto, cartDto);
    }

    @Test
    @SneakyThrows
    public void getOrdersTest(){
        when(profileRepository.existsProfileById(any(UUID.class))).thenReturn(true);
        when(orderRepository.findByCreatedBy_Id(any(UUID.class)))
                .thenReturn(new ArrayList<>(Collections.singletonList(lst.order)));
        MvcResult mvcResult = mockMvc.perform(get("/api/profile/orders")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        OrderDto[] orders = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderDto[].class);
        assertEquals(1, orders.length);
        assertEquals(lst.orderDto, orders[0]);
    }

    @Test
    @SneakyThrows
    public void getAllProfilesTest(){
        when(profileRepository.findAll()).thenReturn(Collections.singletonList(lst.profile));
        MvcResult mvcResult = mockMvc.perform(get("/api/profile/all")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken()))
                .andExpect(status().isOk())
                .andReturn();
        ReadProfileDto[] profiles = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReadProfileDto[].class);
        assertEquals(1, profiles.length);
        assertEquals(lst.profileDto, profiles[0]);
    }

    @Test
    @SneakyThrows
    public void getProfileByIdTest(){
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        MvcResult mvcResult = mockMvc.perform(get("/api/profile/" + lst.profileId)
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken()))
                .andExpect(status().isOk())
                .andReturn();
        ReadProfileDto profileDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReadProfileDto.class);
        assertEquals(lst.profileDto, profileDto);
    }

    @Test
    @SneakyThrows
    public void updateProfileInfoTest(){
        when(profileRepository.findById(any(UUID.class))).thenReturn(Optional.of(lst.profile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));
        Instant updatedAt = lst.profile.getUpdatedAt();
        UpdateProfileDto update = UpdateProfileDto.builder()
                .firstname("test")
                .lastname("test")
                .build();
        MvcResult mvcResult = mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk()).andReturn();
        ReadProfileDto profile = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReadProfileDto.class);
        assertEquals(update.getFirstname(), profile.getFirstName());
        assertEquals(update.getLastname(), profile.getLastName());
        assertNotEquals(updatedAt, profile.getUpdatedAt());
    }

    @Test
    @SneakyThrows
    public void updateProfileInfoTestWithError(){

        UpdateProfileDto update = UpdateProfileDto.builder()
                .firstname("test")
                .lastname("")
                .build();
        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void updateProfileRoleTest(){
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));
        Instant updatedAt = lst.profile.getUpdatedAt();
        String role = Role.ADMIN.name();
        MvcResult mvcResult = mockMvc.perform(put("/api/profile/" + lst.profileId)
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\""+role+"\"")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk()).andReturn();
        ReadProfileDto profile = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ReadProfileDto.class);
        assertEquals(Role.ADMIN, profile.getRole());
        assertNotEquals(updatedAt, profile.getUpdatedAt());
    }

    @Test
    @SneakyThrows
    public void updateProfileRoleTestWithError(){

        String role = Role.ADMIN.name();
        mockMvc.perform(put("/api/profile/" + lst.profileId)
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\""+role+"\"")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
        when(profileRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/profile/" + lst.profileId)
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\""+role+"\"")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteProfileTest(){
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        String id = lst.profileId.toString();
        mockMvc.perform(delete("/api/profile")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\""+id+"\"")
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    public void deleteProfileWithError(){
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.empty());
        String id = lst.profileId.toString();
        mockMvc.perform(delete("/api/profile")
                .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\""+id+"\"")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        when(profileRepository.findById(lst.profileId)).thenReturn(Optional.of(lst.profile));
        mockMvc.perform(delete("/api/profile")
                .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\""+id+"\"")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }
}
