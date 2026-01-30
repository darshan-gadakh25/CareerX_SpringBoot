package com.careerx.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class Payment extends BaseEntity {

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private Users student;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "razorpay_order_id", nullable = false)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Builder.Default
    @Column(name = "payment_status")
    private String paymentStatus = "Pending"; // Pending, Completed, Failed

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "payment_type")
    private String paymentType; // ROADMAP or ASSESSMENT
}
