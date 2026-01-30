package com.careerx.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roadmaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class Roadmap extends BaseEntity {

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private Users student;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    @ToString.Exclude
    private Payment payment;

    @Column(name = "career_option", columnDefinition = "LONGTEXT", nullable = false)
    private String careerOption; // Top 3 career options

    @Column(name = "roadmap_json", columnDefinition = "LONGTEXT", nullable = false)
    private String roadmapJson; // Full roadmap details

    @Column(name = "roadmap_html", columnDefinition = "LONGTEXT")
    private String roadmapHtml; // Formatted roadmap

    @Builder.Default
    @Column(name = "is_email_sent")
    private boolean emailSent = false;
}
