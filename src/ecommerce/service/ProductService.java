package ecommerce.service;

import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;
import ecommerce.infrastructure.ProductRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void register(Product product) {
        productRepository.save(product);
    }

    public void remove(String productId) {
        productRepository.remove(productId);
    }

    public Product getProduct(String productId) {
        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchByName(String query) {
        return productRepository.searchByName(query);
    }

    public List<Product> searchByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public boolean reduceStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId());
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }
        for (OrderItem item : items) {
            productRepository.findById(item.getProductId()).reduceStock(item.getQuantity());
        }
        return true;
    }

    public void restoreStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId());
            if (product != null) {
                product.restoreStock(item.getQuantity());
            }
        }
    }
}
