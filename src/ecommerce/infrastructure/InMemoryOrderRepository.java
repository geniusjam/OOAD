package ecommerce.infrastructure;

import ecommerce.domain.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> database = new HashMap<>();

    @Override
    public void save(Order order) {
        database.put(order.getOrderId(), order);
    }

    @Override
    public Order findById(String orderId) {
        return database.get(orderId);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(database.values());
    }
}
