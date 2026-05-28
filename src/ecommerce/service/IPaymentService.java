package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;

public interface IPaymentService {
    PaymentStatus processPayment(Order order, PaymentMethod method, String paymentDetails);
    void refund(String orderId);
}
