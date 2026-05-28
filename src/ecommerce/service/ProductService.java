package ecommerce.service;

import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductService {
    private final Map<String, Product> catalog = new HashMap<>();

    public void register(Product product) {
        catalog.put(product.getProductId(), product);
    }

    public void remove(String productId) {
        catalog.remove(productId);
    }

    public Product getProduct(String productId) {
        return catalog.get(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(catalog.values());
    }

    public List<Product> searchByName(String query) {
        String q = query.toLowerCase();
        return catalog.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Product> searchByCategory(String category) {
        String q = category.toLowerCase();
        return catalog.values().stream()
                .filter(p -> p.getCategory().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public boolean reduceStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = catalog.get(item.getProductId());
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }
        for (OrderItem item : items) {
            catalog.get(item.getProductId()).reduceStock(item.getQuantity());
        }
        return true;
    }

    public void restoreStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = catalog.get(item.getProductId());
            if (product != null) {
                product.restoreStock(item.getQuantity());
            }
        }
    }
}
