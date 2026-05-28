package ecommerce.service;

import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;
import ecommerce.infrastructure.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        InMemoryProductRepository repo = new InMemoryProductRepository();
        service = new ProductService(repo);
        service.register(new Product("P1", "Alpha Widget", "desc", "Tools", 10.0, 15));
        service.register(new Product("P2", "Beta Gadget", "desc", "Electronics", 25.0, 5));
    }

    @Test
    void getProduct_returnsRegisteredProduct() {
        assertNotNull(service.getProduct("P1"));
        assertEquals("Alpha Widget", service.getProduct("P1").getName());
    }

    @Test
    void getProduct_returnsNullForUnknown() {
        assertNull(service.getProduct("UNKNOWN"));
    }

    @Test
    void getAllProducts_returnsAllRegistered() {
        assertEquals(2, service.getAllProducts().size());
    }

    @Test
    void remove_deletesProduct() {
        service.remove("P1");
        assertNull(service.getProduct("P1"));
    }

    @Test
    void updateProduct_changesAllFields() {
        assertTrue(service.updateProduct("P1", "Updated", "new desc", "Misc", 99.0, 50));
        Product p = service.getProduct("P1");
        assertEquals("Updated", p.getName());
        assertEquals("new desc", p.getDescription());
        assertEquals("Misc", p.getCategory());
        assertEquals(99.0, p.getPrice(), 0.001);
        assertEquals(50, p.getStockQuantity());
    }

    @Test
    void updateProduct_returnsFalseForUnknown() {
        assertFalse(service.updateProduct("UNKNOWN", "X", "X", "X", 1.0, 1));
    }

    @Test
    void searchByName_findsMatchingProducts() {
        List<Product> results = service.searchByName("widget");
        assertEquals(1, results.size());
        assertEquals("P1", results.get(0).getProductId());
    }

    @Test
    void searchByName_returnsCaseInsensitive() {
        assertEquals(1, service.searchByName("ALPHA").size());
    }

    @Test
    void searchByCategory_findsMatchingProducts() {
        List<Product> results = service.searchByCategory("Electronics");
        assertEquals(1, results.size());
        assertEquals("P2", results.get(0).getProductId());
    }

    @Test
    void reduceStock_reducesAvailableStock() {
        List<OrderItem> items = List.of(new OrderItem("P1", 5, 10.0));
        assertTrue(service.reduceStock(items));
        assertEquals(10, service.getProduct("P1").getStockQuantity());
    }

    @Test
    void reduceStock_returnsFalseWhenInsufficientStock() {
        List<OrderItem> items = List.of(new OrderItem("P2", 10, 25.0));
        assertFalse(service.reduceStock(items));
        assertEquals(5, service.getProduct("P2").getStockQuantity());
    }

    @Test
    void reduceStock_doesNotPartiallyReduceOnFailure() {
        // P1 has stock 15, P2 has stock 5 — order 6 of P2 should fail, leaving P1 untouched
        List<OrderItem> items = List.of(
            new OrderItem("P1", 5, 10.0),
            new OrderItem("P2", 6, 25.0)
        );
        assertFalse(service.reduceStock(items));
        assertEquals(15, service.getProduct("P1").getStockQuantity());
    }

    @Test
    void restoreStock_increasesStock() {
        List<OrderItem> items = List.of(new OrderItem("P1", 5, 10.0));
        service.restoreStock(items);
        assertEquals(20, service.getProduct("P1").getStockQuantity());
    }
}
