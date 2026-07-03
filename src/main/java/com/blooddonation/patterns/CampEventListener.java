package com.blooddonation.patterns;

import com.blooddonation.model.Donator;
import com.blooddonation.repository.DonatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampEventListener {

    @Autowired
    private DonatorRepository donatorRepository;

    @Autowired
    @Qualifier("emailServiceAdapter")
    private NotificationService notificationService;

    @Async
    @EventListener
    public void handleCampPublishedEvent(CampPublishedEvent event) {
        String message = "New camp scheduled: " + event.getCamp().getName() + " at " + event.getCamp().getLocation();
        
        // This is now done asynchronously, bypassing the HTTP thread
        List<Donator> allDonators = donatorRepository.findAll();
        for (Donator d : allDonators) {
            notificationService.sendNotification(d.getEmail(), message);
        }
    }
}
