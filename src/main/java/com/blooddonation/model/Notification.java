package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "notification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id")
    private java.util.UUID notificationId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "date_sent", nullable = false)
    private LocalDate dateSent;

    @Column(name = "is_read")
    private boolean isRead = false;

    public void send() {
        System.out.println("Notification sent to " + recipient + ": " + message);
    }
}
