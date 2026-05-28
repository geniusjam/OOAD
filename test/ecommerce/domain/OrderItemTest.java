package ecommerce.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    @Test
    void getSubtotal_returnsUnitPriceTimesQuantity() {
        OrderItem item = new OrderItem("P1", 4, 7.50);
        assertEquals(30.0, item.getSubtotal(), 0.001);
    }

    @Test
    void setQuantity_updatesQuantity() {
        OrderItem item = new OrderItem("P1", 1, 5.0);
        item.setQuantity(3);
        assertEquals(3, item.getQuantity());
    }

    @Test
    void getters_returnConstructorValues() {
        OrderItem item = new OrderItem("P42", 2, 12.0);
        assertEquals("P42", item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(12.0, item.getUnitPrice(), 0.001);
    }
}
