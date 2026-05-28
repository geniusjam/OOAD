package ecommerce.service;

import ecommerce.domain.Order;

public interface OrderObserver {
    void onOrderStatusChanged(Order order);
}
