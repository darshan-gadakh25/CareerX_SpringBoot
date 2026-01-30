package com.careerx.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "id"))
@ToString(callSuper = true)
public class AssessmentSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @ToString.Exclude
    private Payment payment;

    @Column(name = "questions_json", columnDefinition = "LONGTEXT", nullable = false)
    private String questionsJson;

    @Column(name = "user_answers_json", columnDefinition = "LONGTEXT")
    private String userAnswersJson;

    @Column(name = "career_report_json", columnDefinition = "LONGTEXT")
    private String careerReportJson;

    @Column(name = "score")
    private Double score;

    @Column(name = "webcam_recording_url", columnDefinition = "LONGTEXT")
    private String webcamRecordingUrl;

    @Builder.Default
    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
