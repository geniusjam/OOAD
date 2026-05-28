package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;

import java.util.HashMap;
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
        if (product == null) {
            System.out.println("Product not found: " + productId);
            return false;
        }
        if (product.getStockQuantity() < quantity) {
            System.out.println("Not enough stock. Available: " + product.getStockQuantity());
            return false;
        }
        getCart(customerId).addItem(new OrderItem(productId, quantity, product.getPrice()));
        return true;
    }

    public boolean removeItem(String customerId, String productId) {
        Cart cart = carts.get(customerId);
        if (cart == null) return false;
        return cart.removeItem(productId);
    }

    public void clearCart(String customerId) {
        Cart cart = carts.get(customerId);
        if (cart != null) cart.clear();
    }
}
