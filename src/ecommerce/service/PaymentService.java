package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderItem;
import ecommerce.domain.Payment;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;

import java.util.HashMap;
import java.util.Map;

public class PaymentService implements IPaymentService {
    private final Map<String, Payment> payments = new HashMap<>();

    @Override
    public PaymentStatus processPayment(Order order, PaymentMethod method, String paymentDetails) {
        PaymentStrategy strategy = selectStrategy(method);

        double amount = order.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        Payment payment = new Payment("PAY-" + System.currentTimeMillis(), order.getOrderId(), amount, method);
        payment.setStatus(PaymentStatus.VALIDATING);

        if (!strategy.validate(paymentDetails)) {
            payment.setStatus(PaymentStatus.DECLINED);
            payments.put(order.getOrderId(), payment);
            return payment.getStatus();
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setStatus(strategy.pay(amount, paymentDetails));
        payments.put(order.getOrderId(), payment);
        return payment.getStatus();
    }

    @Override
    public void refund(String orderId) {
        Payment payment = payments.get(orderId);
        if (payment == null) {
            System.out.println("No payment record found for order: " + orderId);
            return;
        }
        System.out.println("Refund of $" + payment.getAmount() + " processed for order " + orderId + ".");
    }

    private PaymentStrategy selectStrategy(PaymentMethod method) {
        return switch (method) {
            case MASTERCARD -> new MastercardStrategy();
            case VISA       -> new VisaStrategy();
            case TROY       -> new TroyStrategy();
        };
    }
}
