package ecommerce.service;

import ecommerce.domain.Notification;
import ecommerce.domain.Order;

public class NotificationService implements OrderObserver {

    @Override
    public void onOrderStatusChanged(Order order) {
        String message = "Order " + order.getOrderId() + " is now " + order.getStatus() + ".";
        Notification notification = new Notification(order.getCustomerId(), message);
        notification.send();
    }
}
