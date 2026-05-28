package ecommerce.domain;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String customerId;
    private List<OrderItem> items;

    public Cart(String customerId) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public void addItem(OrderItem item) { this.items.add(item); }

    public boolean removeItem(String productId) {
        return items.removeIf(i -> i.getProductId().equals(productId));
    }

    public void clear() { this.items.clear(); }
}
