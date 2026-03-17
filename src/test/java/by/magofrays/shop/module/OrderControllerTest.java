package by.magofrays.shop.module;

import by.magofrays.shop.dto.CartItemDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.UpdateOrderDto;
import by.magofrays.shop.dto.UpdateOrderStatus;
import by.magofrays.shop.entity.Item;
import by.magofrays.shop.entity.Order;
import by.magofrays.shop.entity.OrderItem;
import by.magofrays.shop.entity.OrderStatus;
import by.magofrays.shop.repository.*;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenGenerator tokenGenerator;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderItemRepository orderItemRepository;

    @MockBean
    private JavaMailSender mailSender;


    @MockBean
    private CartItemRepository cartItemRepository;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private ItemRepository itemRepository;

    private LocalStorageTest localStorage;

    @BeforeEach
    void setUp() {
        localStorage = new LocalStorageTest();
    }

    @Test
    @SneakyThrows
    public void createOrder_AsClient_Success() {
        CartItemDto cartItemDto = localStorage.cartItemDto1;
        List<CartItemDto> items = Arrays.asList(cartItemDto);

        when(profileRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(localStorage.profile));
        when(cartItemRepository.findById(localStorage.cartItem1.getId())).thenReturn(java.util.Optional.of(localStorage.cartItem1));
        when(itemRepository.save(any(Item.class))).thenReturn(localStorage.item1);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(localStorage.orderItem1);
        when(orderRepository.save(any(Order.class))).thenReturn(localStorage.order);

        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        MvcResult result = mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(items)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertNotNull(orderDto);
        assertEquals(localStorage.orderId, orderDto.getId());
    }

    @Test
    @SneakyThrows
    public void createOrder_AsAdmin_Success() {
        CartItemDto cartItemDto = localStorage.cartItemDto1;
        List<CartItemDto> items = Arrays.asList(cartItemDto);

        when(profileRepository.findById(any(UUID.class))).thenReturn(Optional.of(localStorage.profile));
        when(cartItemRepository.findById(localStorage.cartItem1.getId())).thenReturn(Optional.of(localStorage.cartItem1));
        when(itemRepository.save(any(Item.class))).thenReturn(localStorage.item1);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(localStorage.orderItem1);
        when(orderRepository.save(any(Order.class))).thenReturn(localStorage.order);

        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        MvcResult result = mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(items)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertNotNull(orderDto);
    }

    @Test
    @SneakyThrows
    public void createOrder_EmptyItems_ShouldReturnBadRequest() {
        List<CartItemDto> items = Arrays.asList();

        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(items)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void createOrder_Forbidden() {
        CartItemDto cartItemDto = localStorage.cartItemDto1;
        List<CartItemDto> items = Arrays.asList(cartItemDto);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(items)))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getOrderById_AsClient_Success() {
        when(orderRepository.findById(localStorage.orderId)).thenReturn(java.util.Optional.of(localStorage.order));

        MvcResult result = mockMvc.perform(get("/api/order/{orderId}", localStorage.orderId)
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken()))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertEquals(localStorage.orderId, orderDto.getId());
    }

    @Test
    @SneakyThrows
    public void getOrderById_AsAdmin_Success() {
        when(orderRepository.findById(localStorage.orderId)).thenReturn(java.util.Optional.of(localStorage.order));

        MvcResult result = mockMvc.perform(get("/api/order/{orderId}", localStorage.orderId)
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken()))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertEquals(localStorage.orderId, orderDto.getId());
    }

    @Test
    @SneakyThrows
    public void getOrderById_NotFound() {
        when(orderRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/order/{orderId}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getOrderById_Forbidden() {
        mockMvc.perform(get("/api/order/{orderId}", localStorage.orderId))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void updateOrder_AsClient_Success() {
        CartItemDto cartItemDto = localStorage.cartItemDto1;

        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(localStorage.orderId)
                .items(Arrays.asList(cartItemDto))
                .build();

        when(orderRepository.findById(localStorage.orderId)).thenReturn(java.util.Optional.of(localStorage.order));
        when(itemRepository.findById(localStorage.item1.getId())).thenReturn(java.util.Optional.of(localStorage.item1));
        when(itemRepository.save(any(Item.class))).thenReturn(localStorage.item1);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(localStorage.orderItem1);
        when(orderRepository.save(any(Order.class))).thenReturn(localStorage.order);
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        MvcResult result = mockMvc.perform(put("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertNotNull(orderDto);
    }

    @Test
    @SneakyThrows
    public void updateOrder_AsAdmin_Success() {
        CartItemDto cartItemDto = localStorage.cartItemDto1;

        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(localStorage.orderId)
                .items(Arrays.asList(cartItemDto))
                .build();

        when(orderRepository.findById(localStorage.orderId)).thenReturn(java.util.Optional.of(localStorage.order));
        when(itemRepository.findById(localStorage.item1.getId())).thenReturn(java.util.Optional.of(localStorage.item1));
        when(itemRepository.save(any(Item.class))).thenReturn(localStorage.item1);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(localStorage.orderItem1);
        when(orderRepository.save(any(Order.class))).thenReturn(localStorage.order);
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        MvcResult result = mockMvc.perform(put("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertNotNull(orderDto);
    }

    @Test
    @SneakyThrows
    public void updateOrder_EmptyItems_ShouldReturnBadRequest() {
        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(localStorage.orderId)
                .items(Arrays.asList())
                .build();

        mockMvc.perform(put("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void updateOrder_NotFound() {
        CartItemDto cartItemDto = localStorage.cartItemDto1;

        UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
                .orderId(UUID.randomUUID())
                .items(Arrays.asList(cartItemDto))
                .build();

        when(orderRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/api/order")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void updateOrderStatus_AsAdmin_Success() {
        UpdateOrderStatus updateOrderStatus = UpdateOrderStatus.builder()
                .orderId(localStorage.orderId)
                .orderStatus(OrderStatus.PAID)
                .build();

        when(orderRepository.findById(localStorage.orderId)).thenReturn(java.util.Optional.of(localStorage.order));
        when(orderRepository.save(any(Order.class))).thenReturn(localStorage.order);

        MvcResult result = mockMvc.perform(put("/api/order/status")
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderStatus)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto orderDto = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        assertNotNull(orderDto);
    }

    @Test
    @SneakyThrows
    public void updateOrderStatus_AsClient_ShouldReturnForbidden() {
        UpdateOrderStatus updateOrderStatus = UpdateOrderStatus.builder()
                .orderId(localStorage.orderId)
                .orderStatus(OrderStatus.PAID)
                .build();

        mockMvc.perform(put("/api/order/status")
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderStatus)))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void updateOrderStatus_NotFound() {
        UpdateOrderStatus updateOrderStatus = UpdateOrderStatus.builder()
                .orderId(UUID.randomUUID())
                .orderStatus(OrderStatus.PAID)
                .build();

        when(orderRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/api/order/status")
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderStatus)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteOrder_AsAdmin_Success() {
        when(orderRepository.findById(localStorage.orderId)).thenReturn(java.util.Optional.of(localStorage.order));
        doNothing().when(orderRepository).delete(any(Order.class));

        mockMvc.perform(delete("/api/order/{orderId}", localStorage.orderId)
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    public void deleteOrder_AsClient_ShouldReturnForbidden() {
        mockMvc.perform(delete("/api/order/{orderId}", localStorage.orderId)
                        .header("Authorization", "Bearer " + tokenGenerator.getUserToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void deleteOrder_NotFound() {
        when(orderRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(delete("/api/order/{orderId}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + tokenGenerator.getAdminToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteOrder_Forbidden() {
        mockMvc.perform(delete("/api/order/{orderId}", localStorage.orderId))
                .andExpect(status().isForbidden());
    }
}