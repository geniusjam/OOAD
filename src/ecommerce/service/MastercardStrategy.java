package ecommerce.service;

import ecommerce.domain.PaymentStatus;

public class MastercardStrategy implements PaymentStrategy {
    @Override
    public boolean validate(String paymentDetails) {
        return true;
    }

    @Override
    public PaymentStatus pay(double amount, String details) {
        return PaymentStatus.APPROVED;
    }
}
