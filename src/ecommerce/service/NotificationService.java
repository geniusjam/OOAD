package ecommerce.service;

import ecommerce.domain.Order;

public class NotificationService implements OrderObserver {

    @Override
    public void onOrderStatusChanged(Order order) {
        System.out.println("[Notification -> " + order.getCustomerId() + "] "
                + "Order " + order.getOrderId() + " is now " + order.getStatus() + ".");
    }
}
