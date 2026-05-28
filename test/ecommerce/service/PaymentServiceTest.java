package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderItem;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private final PaymentService service = new PaymentService();

    private Order makeOrder(double price, int qty) {
        return new Order("ORD-1", "cust1", List.of(new OrderItem("P1", qty, price)));
    }

    @Test
    void mastercard_alwaysApproves() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 2), PaymentMethod.MASTERCARD, "");
        assertEquals(PaymentStatus.APPROVED, status);
    }

    @Test
    void mastercard_approvesWithNullDetails() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 1), PaymentMethod.MASTERCARD, null);
        assertEquals(PaymentStatus.APPROVED, status);
    }

    @Test
    void visa_approvesWithValidDetails() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 1), PaymentMethod.VISA, "4111111111111111");
        assertEquals(PaymentStatus.APPROVED, status);
    }

    @Test
    void visa_declinesWithBlankDetails() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 1), PaymentMethod.VISA, "");
        assertEquals(PaymentStatus.DECLINED, status);
    }

    @Test
    void visa_declinesWithNullDetails() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 1), PaymentMethod.VISA, null);
        assertEquals(PaymentStatus.DECLINED, status);
    }

    @Test
    void troy_approvesWithValidDetails() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 1), PaymentMethod.TROY, "valid-card");
        assertEquals(PaymentStatus.APPROVED, status);
    }

    @Test
    void troy_declinesWithBlankDetails() {
        PaymentStatus status = service.processPayment(makeOrder(10.0, 1), PaymentMethod.TROY, "   ");
        assertEquals(PaymentStatus.DECLINED, status);
    }
}
