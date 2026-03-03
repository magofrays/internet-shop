package by.magofrays.shop.unittest;

import by.magofrays.shop.service.FileStorageService;
import by.magofrays.shop.service.MailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private MailService mailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> simpleMailMessageCaptor;


    private static final String TO = "test@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String TEXT = "Test Message";

    @Test
    void sendSimpleEmail_ShouldSendEmailWithCorrectData() {
        mailService.sendSimpleEmail(TO, SUBJECT, TEXT);
        verify(mailSender, times(1)).send(simpleMailMessageCaptor.capture());
        SimpleMailMessage sentMessage = simpleMailMessageCaptor.getValue();
        Assertions.assertNotNull(sentMessage);
        Assertions.assertArrayEquals(new String[]{TO}, sentMessage.getTo());
        Assertions.assertEquals(SUBJECT, sentMessage.getSubject());
        Assertions.assertEquals(TEXT, sentMessage.getText());
    }


    @Test
    void sendEmailWithAttachment_WithValidFile_ShouldSendEmail() throws Exception {
        String url = "/somewhere/wow";
        String filename = "receipt.pdf";
        byte[] infoBytes = "testetste".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(infoBytes){
            @Override
            public String getFilename(){
                return filename;
            }
        };
        when(fileStorageService.getFileByPath(url)).thenReturn(resource);
        JavaMailSenderImpl realMailSender = new JavaMailSenderImpl();
        MimeMessage realMimeMessage = realMailSender.createMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage);
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        mailService.sendEmailWithAttachment(TO, SUBJECT, TEXT, url);
        verify(mailSender, times(1)).send(captor.capture());
        MimeMessage mimeMessage = captor.getValue();
        Address[] recipients = mimeMessage.getRecipients(Message.RecipientType.TO);
        Assertions.assertNotNull(recipients);
        Assertions.assertEquals(1, recipients.length);
        Assertions.assertEquals(TO, ((InternetAddress) recipients[0]).getAddress());
        Assertions.assertEquals(SUBJECT, mimeMessage.getSubject());
        Object content = mimeMessage.getContent();
        Assertions.assertInstanceOf(Multipart.class, content);
        Multipart multipart = (Multipart) content;
        Assertions.assertEquals(2, multipart.getCount());
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                Assertions.assertEquals(filename, bodyPart.getFileName());
                byte[] attachmentBytes = StreamUtils.copyToByteArray(bodyPart.getInputStream());
                Assertions.assertArrayEquals(infoBytes, attachmentBytes);
                break;
            }
        }
        verify(fileStorageService, times(1)).getFileByPath(url);
    }
}