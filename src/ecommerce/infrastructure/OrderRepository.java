package ecommerce.infrastructure;

import ecommerce.domain.Order;

public interface OrderRepository {
    void save(Order order);
    Order findById(String orderId);
}
