package org.prod.tgk.services;

import org.prod.tgk.entitys.User;
import org.prod.tgk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private UserRepository userRepository;

    public void send(String mailTo, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailTo);
        message.setSubject(subject);
        message.setText(text);
        this.emailSender.send(message);
    }

    public void activate(User u) {
        send(u.getEmail(), "Активация аккаунта | ТГК", "Для активации перейдите по ссылке: http://localhost:8080/activation?code=" + u.getActivationCode());
    }
}
