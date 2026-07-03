package com.blooddonation.patterns;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceAdapter implements NotificationService {

    @Override
    public void sendNotification(String recipient, String message) {
        // Simulating external Email API
        System.out.println("Email sent to " + recipient + ": " + message);
    }
}
