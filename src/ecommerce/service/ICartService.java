package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.OrderItem;

import java.util.List;

public interface ICartService {
    Cart getCart(String customerId);
    boolean addItem(String customerId, String productId, int quantity);
    boolean removeItem(String customerId, String productId);
    boolean updateQuantity(String customerId, String productId, int quantity);
    void clearCart(String customerId);
    List<OrderItem> getOrderItems(String customerId);
}
