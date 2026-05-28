package ecommerce.domain;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String customerId;
    private List<CartItem> items;

    public Cart(String customerId) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    public String getCustomerId() { return customerId; }
    public List<CartItem> getItems() { return items; }
    public void addItem(CartItem item) { this.items.add(item); }

    public boolean removeItem(String productId) {
        return items.removeIf(i -> i.getProduct().getProductId().equals(productId));
    }

    public boolean updateQuantity(String productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return true;
            }
        }
        return false;
    }

    public void clear() { this.items.clear(); }
}
