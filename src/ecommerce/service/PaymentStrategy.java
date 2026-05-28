package ecommerce.service;

import ecommerce.domain.PaymentStatus;

public interface PaymentStrategy {
    boolean validate(String paymentDetails);
    PaymentStatus pay(double amount, String details);
}
