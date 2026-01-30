package com.careerx.serviceImpl;

import com.careerx.apirequests.CreateOrderRequest;
import com.careerx.apirequests.VerifyPaymentRequest;
import com.careerx.entities.Payment;
import com.careerx.entities.Users;
import com.careerx.repository.PaymentRepository;
import com.careerx.repository.UserRepository;
import com.careerx.services.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository usersRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public Map<String, Object> createOrder(Long userId, CreateOrderRequest request) {
        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (request.getAmount() * 100)); // in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_" + System.currentTimeMillis());

            Order order = client.orders.create(orderRequest);
            String orderId = order.get("id");

            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Payment payment = Payment.builder()
                    .student(user)
                    .amount(request.getAmount())
                    .razorpayOrderId(orderId)
                    .paymentStatus("Pending")
                    .paymentType(request.getRequestType())
                    .build();

            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("amount", request.getAmount() * 100);
            response.put("keyId", razorpayKeyId);
            response.put("paymentId", payment.getId());
            response.put("currency", "INR");

            return response;
        } catch (RazorpayException e) {
            throw new RuntimeException("Error creating Razorpay order: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyPayment(Long userId, VerifyPaymentRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!isValid) {
                throw new RuntimeException("Invalid payment signature");
            }

            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment record not found"));

            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setPaymentStatus("Completed");
            payment.setCompletedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("message", "Payment verified successfully");

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    @Override
    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.findByStudentIdOrderByCreatedDateDesc(userId);
    }

    @Override
    public String getReceiptHtml(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findByIdAndStudentIdAndPaymentStatus(paymentId, userId, "Completed")
                .orElseThrow(() -> new RuntimeException("Payment not found or not completed"));

        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Payment Receipt</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .receipt { border: 1px solid #ddd; padding: 20px; max-width: 600px; }
                        .header { text-align: center; color: #2F4156; }
                        .details { margin: 20px 0; }
                        .detail-row { display: flex; justify-content: space-between; margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <div class='receipt'>
                        <h1 class='header'>Payment Receipt</h1>
                        <div class='details'>
                            <div class='detail-row'>
                                <strong>Payment ID:</strong>
                                <span>%d</span>
                            </div>
                            <div class='detail-row'>
                                <strong>Order ID:</strong>
                                <span>%s</span>
                            </div>
                            <div class='detail-row'>
                                <strong>Amount:</strong>
                                <span>₹%.2f</span>
                            </div>
                            <div class='detail-row'>
                                <strong>Status:</strong>
                                <span>%s</span>
                            </div>
                            <div class='detail-row'>
                                <strong>Date:</strong>
                                <span>%s</span>
                            </div>
                            <div class='detail-row'>
                                <strong>Student:</strong>
                                <span>%s</span>
                            </div>
                        </div>
                        <p style='text-align: center; margin-top: 30px; color: #666;'>
                            Thank you for your payment!
                        </p>
                    </div>
                </body>
                </html>
                """,
                payment.getId(),
                payment.getRazorpayOrderId(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getCompletedAt().toString(),
                payment.getStudent().getName());
    }
}
