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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private Resource resource;

    @InjectMocks
    private MailService mailService;
    @Captor
    private ArgumentCaptor<SimpleMailMessage> simpleMailMessageCaptor;

    private static final String TO = "test@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String TEXT = "Test Message";

    @Test
    void sendSimpleEmail_ShouldSendEmailWithCorrectData() {
        // When
        mailService.sendSimpleEmail(TO, SUBJECT, TEXT);

        // Then
        verify(mailSender, times(1)).send(simpleMailMessageCaptor.capture());

        SimpleMailMessage sentMessage = simpleMailMessageCaptor.getValue();
        Assertions.assertNotNull(sentMessage);
        Assertions.assertArrayEquals(new String[]{TO}, sentMessage.getTo());
        Assertions.assertEquals(SUBJECT, sentMessage.getSubject());
        Assertions.assertEquals(TEXT, sentMessage.getText());
    }


    @Test
    void sendEmailWithAttachment_WithValidFile_ShouldSendEmail() throws Exception {
        // Given
        String attachmentPath = "classpath:test.txt";
        java.io.File tempFile = java.io.File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        Properties props = new Properties();
        Session session = Session.getInstance(props, null);
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
        when(fileStorageService.getFileByPath(attachmentPath)).thenReturn(resource);
        when(resource.getFile()).thenReturn(tempFile);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

        // When
        mailService.sendEmailWithAttachment(TO, SUBJECT, TEXT, attachmentPath);
        // Then
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
        verify(fileStorageService, times(1)).getFileByPath(attachmentPath);
        verify(resource, times(1)).getFile();
    }
}