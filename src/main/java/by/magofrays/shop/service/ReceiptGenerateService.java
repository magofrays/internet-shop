package by.magofrays.shop.service;

import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.entity.OrderReceipt;
import by.magofrays.shop.repository.OrderReceiptRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Component
public class ReceiptGenerateService {
    private final ITemplateEngine templateEngine;
    private final FileStorageService fileStorageService;
    private final String templateName;
    private final OrderReceiptRepository orderReceiptRepository;

    public ReceiptGenerateService(@Autowired ITemplateEngine templateEngine,
                                  @Autowired FileStorageService fileStorageService,
                                  @Autowired OrderReceiptRepository orderReceiptRepository,
                                  @Value("${receipt.template}") String templateName){
        this.templateEngine = templateEngine;
        this.fileStorageService = fileStorageService;
        this.orderReceiptRepository = orderReceiptRepository;
        this.templateName = templateName;
    }

    public String createReceipt(OrderDto order){
        OrderReceipt orderReceipt = orderReceiptRepository.findById(order.getId())
                .orElse(OrderReceipt.builder()
                        .orderId(order.getId())
                        .build());
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("now", LocalDateTime.now());
        String html = templateEngine.process(templateName, context);
        byte[] pdfBytes = convertHtmlToPdf(html);
        String fileName = "receipt.pdf";
        ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes){
            @Override
            public String getFilename(){
                return fileName;
            }
        };
        String url = fileStorageService.saveFile(pdfResource, "pdf/receipt", order.getId(), orderReceipt.getUrl());
        orderReceipt.setUrl(url);
        orderReceiptRepository.save(orderReceipt);
        return url;
    }

    @SneakyThrows
    private byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
            return os.toByteArray();
        }
    }


}
