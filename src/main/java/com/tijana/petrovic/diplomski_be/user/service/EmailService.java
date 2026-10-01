package com.tijana.petrovic.diplomski_be.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendInvitationEmail(String firstName, String to, String token) throws MessagingException {
        var context = new Context();

        var activationUrl = generateInvitationUrl(token);

        context.setVariable("firstName", firstName);
        context.setVariable("activationUrl", activationUrl);

        var html = templateEngine.process(
                "email/account-activation",
                context
        );

        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject("Activate your account");
        helper.setText(html, true);

        mailSender.send(message);
    }

    private String generateInvitationUrl(String token) {
        return "http://localhost:8080/user/verify?token=" + token;
    }
}
