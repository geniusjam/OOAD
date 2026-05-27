package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.Payment;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;

public class PaymentService {
    public PaymentStatus processPayment(Order order, PaymentMethod method, String paymentDetails) {
        PaymentStrategy strategy = selectStrategy(method);

        Payment payment = new Payment("PAY-" + System.currentTimeMillis(), order.getOrderId());
        payment.setStatus(PaymentStatus.VALIDATING);

        if (!strategy.validate(paymentDetails)) {
            payment.setStatus(PaymentStatus.DECLINED);
            return payment.getStatus();
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        boolean success = strategy.pay(100.0, paymentDetails);

        payment.setStatus(success ? PaymentStatus.APPROVED : PaymentStatus.DECLINED);
        return payment.getStatus();
    }

    private PaymentStrategy selectStrategy(PaymentMethod method) {
        switch (method) {
            case MASTERCARD: return new MastercardStrategy();
            default: throw new IllegalArgumentException("Unknown method");
        }
    }
}
