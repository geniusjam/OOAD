package ecommerce.infrastructure;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<Order> findByCustomer(String customerId) {
        return database.values().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return database.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }
}
