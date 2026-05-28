package ecommerce.infrastructure;

import ecommerce.domain.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product product);
    void remove(String productId);
    Product findById(String productId);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> searchByName(String query);
}
