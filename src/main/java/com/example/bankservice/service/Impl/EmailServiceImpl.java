package com.example.bankservice.service.Impl;

import com.example.bankservice.dto.EmailDetails;
import com.example.bankservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {

    }
}
