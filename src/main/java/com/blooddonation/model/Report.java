package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "report")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id")
    private java.util.UUID reportId;

    @Column(nullable = false)
    private String type;

    @Column(name = "generated_on", nullable = false)
    private LocalDate generatedOn;

    @Column(length = 2000)
    private String content;

    public String export() {
        return "Report [" + type + "] generated on " + generatedOn + ": " + content;
    }
}
