package ecommerce.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        product = new Product("P1", "Widget", "desc", "Tools", 10.0, 20);
        cart = new Cart("cust1");
    }

    @Test
    void newCart_isEmpty() {
        assertEquals("cust1", cart.getCustomerId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void addItem_addsItemToCart() {
        cart.addItem(new CartItem(product, 2));
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void removeItem_removesExistingItem() {
        cart.addItem(new CartItem(product, 2));
        assertTrue(cart.removeItem("P1"));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void removeItem_returnsFalseForMissingProduct() {
        assertFalse(cart.removeItem("NONEXISTENT"));
    }

    @Test
    void updateQuantity_updatesExistingItem() {
        cart.addItem(new CartItem(product, 2));
        assertTrue(cart.updateQuantity("P1", 5));
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void updateQuantity_returnsFalseForMissingProduct() {
        assertFalse(cart.updateQuantity("NONEXISTENT", 3));
    }

    @Test
    void clear_emptiesCart() {
        cart.addItem(new CartItem(product, 1));
        cart.clear();
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void cartItem_getSubtotal_returnsCorrectValue() {
        CartItem item = new CartItem(product, 3);
        assertEquals(30.0, item.getSubtotal(), 0.001);
    }

    @Test
    void cartItem_setQuantity_updatesQuantity() {
        CartItem item = new CartItem(product, 1);
        item.setQuantity(7);
        assertEquals(7, item.getQuantity());
    }
}
