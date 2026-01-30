package com.careerx.apirequests;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Double amount;
    private String requestType; // ROADMAP or ASSESSMENT
}
