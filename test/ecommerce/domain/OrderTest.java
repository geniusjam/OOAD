package ecommerce.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Order makeOrder() {
        List<OrderItem> items = List.of(new OrderItem("P1", 2, 10.0));
        return new Order("ORD-1", "cust1", items);
    }

    @Test
    void newOrder_hasPendingStatus() {
        assertEquals(OrderStatus.PENDING, makeOrder().getStatus());
    }

    @Test
    void newOrder_hasTimestamp() {
        assertNotNull(makeOrder().getTimestamp());
    }

    @Test
    void setStatus_changesStatus() {
        Order order = makeOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void getters_returnConstructorValues() {
        Order order = makeOrder();
        assertEquals("ORD-1", order.getOrderId());
        assertEquals("cust1", order.getCustomerId());
        assertEquals(1, order.getItems().size());
    }
}
