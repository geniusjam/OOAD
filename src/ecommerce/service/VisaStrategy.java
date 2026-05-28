package ecommerce.service;

public class VisaStrategy implements PaymentStrategy {
    @Override
    public boolean validate(String paymentDetails) {
        return paymentDetails != null && !paymentDetails.isBlank();
    }

    @Override
    public boolean pay(double amount, String details) {
        return true;
    }
}
