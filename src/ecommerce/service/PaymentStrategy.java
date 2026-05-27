package ecommerce.service;

public interface PaymentStrategy {
    boolean validate(String paymentDetails);
    boolean pay(double amount, String details);
}
