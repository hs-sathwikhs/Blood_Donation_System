package com.blooddonation.patterns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceAdapter implements NotificationService {
    private final Logger log = LoggerFactory.getLogger(EmailServiceAdapter.class);

    @Override
    public void sendNotification(String recipient, String message) {
        // local delivery simulation through the logging api
        log.info("Camp notice for {}: {}", recipient, message);
    }
}
