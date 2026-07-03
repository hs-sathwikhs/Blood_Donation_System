package com.blooddonation.patterns;

import org.springframework.stereotype.Service;

@Service
public class SMSServiceAdapter implements NotificationService {

    @Override
    public void sendNotification(String recipient, String message) {
        // Simulating external SMS API
        System.out.println("SMS sent to " + recipient + ": " + message);
    }
}
