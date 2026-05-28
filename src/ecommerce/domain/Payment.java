package ecommerce.domain;

import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private String orderId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime timestamp;

    public Payment(String paymentId, String orderId, double amount, PaymentMethod method) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.INITIATED;
        this.timestamp = LocalDateTime.now();
    }

    public String getPaymentId() { return paymentId; }
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}
