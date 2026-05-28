package ecommerce.service;

import ecommerce.domain.PaymentStatus;

public class VisaStrategy implements PaymentStrategy {
    @Override
    public boolean validate(String paymentDetails) {
        return paymentDetails != null && !paymentDetails.isBlank();
    }

    @Override
    public PaymentStatus pay(double amount, String details) {
        return PaymentStatus.APPROVED;
    }
}
