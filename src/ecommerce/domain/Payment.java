package ecommerce.domain;

public class Payment {
    private String paymentId;
    private String orderId;
    private PaymentStatus status;

    public Payment(String paymentId, String orderId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.status = PaymentStatus.INITIATED;
    }

    public void setStatus(PaymentStatus status) { this.status = status; }
    public PaymentStatus getStatus() { return status; }
}
