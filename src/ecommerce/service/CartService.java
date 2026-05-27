package ecommerce.service;

import ecommerce.domain.Cart;
import java.util.HashMap;
import java.util.Map;

public class CartService {
    private Map<String, Cart> carts = new HashMap<>();

    public Cart getCart(String customerId) {
        return carts.computeIfAbsent(customerId, Cart::new);
    }

    public void clearCart(String customerId) {
        Cart cart = carts.get(customerId);
        if (cart != null) {
            cart.clear();
        }
    }
}
