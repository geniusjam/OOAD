package ecommerce.service;

import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private Map<String, Product> catalog = new HashMap<>();

    public void register(Product product) {
        catalog.put(product.getProductId(), product);
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
