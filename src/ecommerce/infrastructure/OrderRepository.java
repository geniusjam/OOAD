package ecommerce.infrastructure;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(String orderId);
    List<Order> findAll();
    List<Order> findByCustomer(String customerId);
    List<Order> findByStatus(OrderStatus status);
}
