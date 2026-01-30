package com.careerx.controller;

import com.careerx.apirequests.CreateOrderRequest;
import com.careerx.apirequests.VerifyPaymentRequest;
import com.careerx.entities.Payment;
import com.careerx.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(Authentication authentication, @RequestBody CreateOrderRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(paymentService.createOrder(userId, request));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(Authentication authentication, @RequestBody VerifyPaymentRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(paymentService.verifyPayment(userId, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(paymentService.getPaymentHistory(userId));
    }

    @GetMapping("/receipt/{paymentId}")
    public ResponseEntity<String> getReceipt(Authentication authentication, @PathVariable Long paymentId) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(paymentService.getReceiptHtml(userId, paymentId));
    }
}
