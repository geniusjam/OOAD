package ecommerce.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    private Product product(int stock) {
        return new Product("P1", "Widget", "A widget", "Tools", 9.99, stock);
    }

    @Test
    void isInStock_returnsTrueWhenStockPositive() {
        assertTrue(product(5).isInStock());
    }

    @Test
    void isInStock_returnsFalseWhenZero() {
        assertFalse(product(0).isInStock());
    }

    @Test
    void reduceStock_decreasesQuantity() {
        Product p = product(10);
        p.reduceStock(3);
        assertEquals(7, p.getStockQuantity());
    }

    @Test
    void restoreStock_increasesQuantity() {
        Product p = product(5);
        p.restoreStock(4);
        assertEquals(9, p.getStockQuantity());
    }

    @Test
    void setters_updateAllFields() {
        Product p = product(1);
        p.setName("Gadget");
        p.setDescription("A gadget");
        p.setCategory("Electronics");
        p.setPrice(19.99);
        p.setStockQuantity(20);
        assertEquals("Gadget", p.getName());
        assertEquals("A gadget", p.getDescription());
        assertEquals("Electronics", p.getCategory());
        assertEquals(19.99, p.getPrice(), 0.001);
        assertEquals(20, p.getStockQuantity());
    }

    @Test
    void getters_returnConstructorValues() {
        Product p = new Product("P99", "Thing", "desc", "Cat", 5.0, 3);
        assertEquals("P99", p.getProductId());
        assertEquals("Thing", p.getName());
        assertEquals("desc", p.getDescription());
        assertEquals("Cat", p.getCategory());
        assertEquals(5.0, p.getPrice(), 0.001);
        assertEquals(3, p.getStockQuantity());
    }
}
