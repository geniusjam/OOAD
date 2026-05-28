package ecommerce.infrastructure;

import ecommerce.domain.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> store = new HashMap<>();

    @Override
    public void save(Product product) {
        store.put(product.getProductId(), product);
    }

    @Override
    public void remove(String productId) {
        store.remove(productId);
    }

    @Override
    public Product findById(String productId) {
        return store.get(productId);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        String q = category.toLowerCase();
        return store.values().stream()
                .filter(p -> p.getCategory().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByName(String query) {
        String q = query.toLowerCase();
        return store.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
}
