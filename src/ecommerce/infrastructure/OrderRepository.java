package ecommerce.infrastructure;

import ecommerce.domain.Order;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(String orderId);
    List<Order> findAll();
}
