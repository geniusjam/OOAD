package ecommerce.infrastructure;

import ecommerce.domain.Order;
import java.util.HashMap;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {
    private Map<String, Order> database = new HashMap<>();

    @Override
    public void save(Order order) {
        database.put(order.getOrderId(), order);
    }

    @Override
    public Order findById(String orderId) {
        return database.get(orderId);
    }
}
