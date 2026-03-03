package by.magofrays.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final FileStorageService fileStorageService;

    public void sendSimpleEmail(String toAddress, String subject, String message) {
        log.info("Sending simple email to address: {}", toAddress);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        mailSender.send(simpleMailMessage);
    }


    public void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException {
        log.info("Sending email with attachment to address: {}", toAddress);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(subject);
        messageHelper.setText(message);
        Resource resource = fileStorageService.getFileByPath(attachment);
        String res = resource.getFilename();
        messageHelper.addAttachment(Objects.requireNonNull(resource.getFilename()), resource);
        mailSender.send(mimeMessage);
    }
}
