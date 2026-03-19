package by.magofrays.shop.unit;

import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.entity.OrderReceipt;
import by.magofrays.shop.repository.OrderReceiptRepository;
import by.magofrays.shop.service.FileStorageService;
import by.magofrays.shop.service.ReceiptGenerateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptGenerateServiceTest {

    @Mock
    OrderReceiptRepository orderReceiptRepository;

    @Mock
    FileStorageService fileStorageService;

    @Mock
    ITemplateEngine templateEngine;

    private ReceiptGenerateService receiptGenerateService;
    private static final String TEMPLATE_NAME = "payment-receipt";

    @BeforeEach
    void setUp() {
        receiptGenerateService = new ReceiptGenerateService(
                templateEngine,
                fileStorageService,
                orderReceiptRepository,
                TEMPLATE_NAME
        );
    }

    @Test
    public void createReceiptTest() {
        UUID orderId = UUID.randomUUID();
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .createdBy(ReadProfileDto.builder()
                        .id(UUID.randomUUID())
                        .build())
                .build();

        String expectedUrl = "save/pdf/receipt/receipt-" + UUID.randomUUID() + "-0";

        when(orderReceiptRepository.findById(orderId)).thenReturn(Optional.empty());

        String htmlContent = "<html>smth</html>";
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenAnswer(invocation -> {
                    String templateName = invocation.getArgument(0);
                    Context context = invocation.getArgument(1);
                    assertEquals("payment-receipt", templateName);
                    assertNotNull(context.getVariable("order"));
                    assertNotNull(context.getVariable("now"));
                    return htmlContent;
                });

        when(fileStorageService.saveFile(any(Resource.class), eq("pdf/receipt"), eq(orderId), isNull()))
                .thenReturn(expectedUrl);

        when(orderReceiptRepository.save(any(OrderReceipt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<OrderReceipt> receiptCaptor = ArgumentCaptor.forClass(OrderReceipt.class);
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        String actualUrl = receiptGenerateService.createReceipt(orderDto);

        assertEquals(expectedUrl, actualUrl);

        verify(fileStorageService).saveFile(resourceCaptor.capture(), eq("pdf/receipt"), eq(orderId), isNull());

        Resource capturedResource = resourceCaptor.getValue();
        assertEquals("receipt.pdf", capturedResource.getFilename());
        assertInstanceOf(ByteArrayResource.class, capturedResource);
        assertDoesNotThrow(capturedResource::contentLength);
        verify(orderReceiptRepository).save(receiptCaptor.capture());

        OrderReceipt savedReceipt = receiptCaptor.getValue();
        assertEquals(orderId, savedReceipt.getOrderId());
        assertEquals(expectedUrl, savedReceipt.getUrl());
    }
}
