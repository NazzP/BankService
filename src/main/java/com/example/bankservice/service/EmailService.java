package com.example.bankservice.service;

import com.example.bankservice.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
