package ecommerce.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private String notificationId;
    private String customerId;
    private String message;
    private LocalDateTime timestamp;

    public Notification(String customerId, String message) {
        this.notificationId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getNotificationId() { return notificationId; }
    public String getCustomerId() { return customerId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void send() {
        System.out.println("[Notification -> " + customerId + "] " + message);
    }
}
