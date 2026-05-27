package ecommerce.service;

public class MastercardStrategy implements PaymentStrategy {
    @Override
    public boolean validate(String paymentDetails) {
        return true;
    }

    @Override
    public boolean pay(double amount, String details) {
        return true;
    }
}
