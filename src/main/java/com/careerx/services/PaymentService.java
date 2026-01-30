package com.careerx.services;

import com.careerx.apirequests.CreateOrderRequest;
import com.careerx.apirequests.VerifyPaymentRequest;
import com.careerx.entities.Payment;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> createOrder(Long userId, CreateOrderRequest request);

    Map<String, Object> verifyPayment(Long userId, VerifyPaymentRequest request);

    List<Payment> getPaymentHistory(Long userId);

    String getReceiptHtml(Long userId, Long paymentId);
}
