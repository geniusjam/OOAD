package ecommerce.service;

public class NotificationService {
    public void notifyCustomer(String customerId, String message) {
        System.out.println("[Notification -> " + customerId + "] " + message);
    }
}
