package ecommerce.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime timestamp;

    public Order(String orderId, String customerId, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
