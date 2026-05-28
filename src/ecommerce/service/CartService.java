package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.CartItem;
import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private final Map<String, Cart> carts = new HashMap<>();
    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public Cart getCart(String customerId) {
        return carts.computeIfAbsent(customerId, Cart::new);
    }

    public boolean addItem(String customerId, String productId, int quantity) {
        Product product = productService.getProduct(productId);
        if (product == null) return false;
        if (product.getStockQuantity() < quantity) return false;
        getCart(customerId).addItem(new CartItem(product, quantity));
        return true;
    }

    public boolean removeItem(String customerId, String productId) {
        Cart cart = carts.get(customerId);
        if (cart == null) return false;
        return cart.removeItem(productId);
    }

    public boolean updateQuantity(String customerId, String productId, int quantity) {
        Product product = productService.getProduct(productId);
        if (product == null) return false;
        if (product.getStockQuantity() < quantity) return false;
        Cart cart = carts.get(customerId);
        if (cart == null) return false;
        return cart.updateQuantity(productId, quantity);
    }

    public void clearCart(String customerId) {
        Cart cart = carts.get(customerId);
        if (cart != null) cart.clear();
    }

    public List<OrderItem> getOrderItems(String customerId) {
        List<OrderItem> result = new ArrayList<>();
        for (CartItem ci : getCart(customerId).getItems()) {
            result.add(new OrderItem(ci.getProduct().getProductId(), ci.getQuantity(), ci.getProduct().getPrice()));
        }
        return result;
    }
}
