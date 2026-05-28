package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderItem;
import ecommerce.domain.Payment;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;

public class PaymentService {

    public PaymentStatus processPayment(Order order, PaymentMethod method, String paymentDetails) {
        PaymentStrategy strategy = selectStrategy(method);

        double amount = order.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        Payment payment = new Payment("PAY-" + System.currentTimeMillis(), order.getOrderId(), amount, method);
        payment.setStatus(PaymentStatus.VALIDATING);

        if (!strategy.validate(paymentDetails)) {
            payment.setStatus(PaymentStatus.DECLINED);
            return payment.getStatus();
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        boolean success = strategy.pay(amount, paymentDetails);

        payment.setStatus(success ? PaymentStatus.APPROVED : PaymentStatus.DECLINED);
        return payment.getStatus();
    }

    private PaymentStrategy selectStrategy(PaymentMethod method) {
        return switch (method) {
            case MASTERCARD -> new MastercardStrategy();
            case VISA       -> new VisaStrategy();
            case TROY       -> new TroyStrategy();
        };
    }
}
