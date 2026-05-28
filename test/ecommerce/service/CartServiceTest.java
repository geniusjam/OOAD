package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.OrderItem;
import ecommerce.domain.Product;
import ecommerce.infrastructure.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        InMemoryProductRepository repo = new InMemoryProductRepository();
        repo.save(new Product("P1", "Widget", "desc", "Tools", 9.99, 10));
        repo.save(new Product("P2", "Gadget", "desc", "Electronics", 19.99, 0));
        cartService = new CartService(new ProductService(repo));
    }

    @Test
    void getCart_createsCartOnFirstCall() {
        Cart cart = cartService.getCart("cust1");
        assertNotNull(cart);
        assertEquals("cust1", cart.getCustomerId());
    }

    @Test
    void addItem_successWhenInStock() {
        assertTrue(cartService.addItem("cust1", "P1", 3));
        assertEquals(1, cartService.getCart("cust1").getItems().size());
    }

    @Test
    void addItem_failsWhenProductNotFound() {
        assertFalse(cartService.addItem("cust1", "MISSING", 1));
    }

    @Test
    void addItem_failsWhenInsufficientStock() {
        assertFalse(cartService.addItem("cust1", "P2", 1));
    }

    @Test
    void removeItem_removesExistingItem() {
        cartService.addItem("cust1", "P1", 1);
        assertTrue(cartService.removeItem("cust1", "P1"));
        assertTrue(cartService.getCart("cust1").getItems().isEmpty());
    }

    @Test
    void removeItem_returnsFalseForNonexistentCart() {
        assertFalse(cartService.removeItem("unknown", "P1"));
    }

    @Test
    void updateQuantity_updatesExistingItem() {
        cartService.addItem("cust1", "P1", 1);
        assertTrue(cartService.updateQuantity("cust1", "P1", 5));
        assertEquals(5, cartService.getCart("cust1").getItems().get(0).getQuantity());
    }

    @Test
    void updateQuantity_failsWhenInsufficientStock() {
        cartService.addItem("cust1", "P1", 1);
        assertFalse(cartService.updateQuantity("cust1", "P1", 100));
    }

    @Test
    void updateQuantity_failsWhenProductNotFound() {
        assertFalse(cartService.updateQuantity("cust1", "MISSING", 1));
    }

    @Test
    void updateQuantity_failsWhenCartDoesNotExist() {
        assertFalse(cartService.updateQuantity("ghost", "P1", 1));
    }

    @Test
    void clearCart_emptiesCart() {
        cartService.addItem("cust1", "P1", 2);
        cartService.clearCart("cust1");
        assertTrue(cartService.getCart("cust1").getItems().isEmpty());
    }

    @Test
    void getOrderItems_returnsSnapshotWithCorrectFields() {
        cartService.addItem("cust1", "P1", 3);
        List<OrderItem> items = cartService.getOrderItems("cust1");
        assertEquals(1, items.size());
        assertEquals("P1", items.get(0).getProductId());
        assertEquals(3, items.get(0).getQuantity());
        assertEquals(9.99, items.get(0).getUnitPrice(), 0.001);
    }
}
