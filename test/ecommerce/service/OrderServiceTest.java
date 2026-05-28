package ecommerce.service;

import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.Product;
import ecommerce.infrastructure.InMemoryOrderRepository;
import ecommerce.infrastructure.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private OrderService orderService;
    private CartService cartService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        InMemoryProductRepository productRepo = new InMemoryProductRepository();
        productRepo.save(new Product("P1", "Widget", "desc", "Tools", 10.0, 5));
        productService = new ProductService(productRepo);
        cartService = new CartService(productService);
        PaymentService paymentService = new PaymentService();
        orderService = new OrderService(cartService, productService, paymentService, new InMemoryOrderRepository());
    }

    // ── placeOrder ──────────────────────────────────────────────────────────

    @Test
    void placeOrder_returnsOrderIdOnSuccess() {
        cartService.addItem("cust1", "P1", 2);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertNotNull(orderId);
    }

    @Test
    void placeOrder_reducesStockOnSuccess() {
        cartService.addItem("cust1", "P1", 2);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertEquals(3, productService.getProduct("P1").getStockQuantity());
    }

    @Test
    void placeOrder_clearsCartOnSuccess() {
        cartService.addItem("cust1", "P1", 2);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertTrue(cartService.getCart("cust1").getItems().isEmpty());
    }

    @Test
    void placeOrder_returnsNullForEmptyCart() {
        assertNull(orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details"));
    }

    @Test
    void placeOrder_returnsNullWhenStockInsufficientAtOrderTime() {
        cartService.addItem("cust1", "P1", 5);
        productService.getProduct("P1").setStockQuantity(2); // stock drops before order
        assertNull(orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details"));
    }

    @Test
    void placeOrder_partialOrder_proceedsWithFulfillableItemsOnly() {
        InMemoryProductRepository extraRepo = new InMemoryProductRepository();
        extraRepo.save(new Product("P1", "Widget", "desc", "Tools", 10.0, 5));
        extraRepo.save(new Product("P2", "Gadget", "desc", "Tools", 5.0, 0));
        ProductService ps = new ProductService(extraRepo);
        CartService cs = new CartService(ps);
        OrderService os = new OrderService(cs, ps, new PaymentService(), new InMemoryOrderRepository());

        cs.addItem("cust1", "P1", 2);
        cs.addItem("cust1", "P2", 1); // out of stock
        String orderId = os.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertNotNull(orderId);
        assertEquals(3, ps.getProduct("P1").getStockQuantity());
    }

    @Test
    void placeOrder_restoresStockWhenPaymentDeclined() {
        cartService.addItem("cust1", "P1", 2);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.VISA, ""); // blank → declined
        assertNull(orderId);
        assertEquals(5, productService.getProduct("P1").getStockQuantity());
    }

    @Test
    void placeOrder_notifiesObserverOnConfirm() {
        cartService.addItem("cust1", "P1", 1);
        boolean[] notified = {false};
        orderService.addObserver(order -> notified[0] = true);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertTrue(notified[0]);
    }

    // ── cancelOrder ─────────────────────────────────────────────────────────

    @Test
    void cancelOrder_cancelsConfirmedOrder() {
        cartService.addItem("cust1", "P1", 2);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertTrue(orderService.cancelOrder("cust1", orderId));
    }

    @Test
    void cancelOrder_restoresStockOnCancel() {
        cartService.addItem("cust1", "P1", 2);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        orderService.cancelOrder("cust1", orderId);
        assertEquals(5, productService.getProduct("P1").getStockQuantity());
    }

    @Test
    void cancelOrder_returnsFalseForUnknownOrder() {
        assertFalse(orderService.cancelOrder("cust1", "ORD-UNKNOWN"));
    }

    @Test
    void cancelOrder_returnsFalseWhenWrongOwner() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertFalse(orderService.cancelOrder("other", orderId));
    }

    @Test
    void cancelOrder_refundsWhenCancellingConfirmedOrder() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertNotNull(orderId);
        assertTrue(orderService.cancelOrder("cust1", orderId));
    }

    @Test
    void cancelOrder_returnsFalseWhenShipped() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        orderService.shipOrder(orderId);
        assertFalse(orderService.cancelOrder("cust1", orderId));
    }

    // ── shipOrder ───────────────────────────────────────────────────────────

    @Test
    void shipOrder_transitionsConfirmedToShipped() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertTrue(orderService.shipOrder(orderId));
        assertEquals(OrderStatus.SHIPPED, orderService.trackOrder("cust1", false, orderId).getStatus());
    }

    @Test
    void shipOrder_returnsFalseForUnknownOrder() {
        assertFalse(orderService.shipOrder("ORD-UNKNOWN"));
    }

    @Test
    void shipOrder_returnsFalseWhenAlreadyShipped() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        orderService.shipOrder(orderId);
        assertFalse(orderService.shipOrder(orderId));
    }

    // ── deliverOrder ────────────────────────────────────────────────────────

    @Test
    void deliverOrder_transitionsShippedToDelivered() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        orderService.shipOrder(orderId);
        assertTrue(orderService.deliverOrder(orderId));
        assertEquals(OrderStatus.DELIVERED, orderService.trackOrder("cust1", false, orderId).getStatus());
    }

    @Test
    void deliverOrder_returnsFalseForUnknownOrder() {
        assertFalse(orderService.deliverOrder("ORD-UNKNOWN"));
    }

    @Test
    void deliverOrder_returnsFalseWhenNotShipped() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertFalse(orderService.deliverOrder(orderId)); // still CONFIRMED
    }

    // ── trackOrder ──────────────────────────────────────────────────────────

    @Test
    void trackOrder_returnsOrderForOwner() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertNotNull(orderService.trackOrder("cust1", false, orderId));
    }

    @Test
    void trackOrder_returnsNullForNonOwner() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertNull(orderService.trackOrder("other", false, orderId));
    }

    @Test
    void trackOrder_adminCanViewAnyOrder() {
        cartService.addItem("cust1", "P1", 1);
        String orderId = orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertNotNull(orderService.trackOrder("admin", true, orderId));
    }

    @Test
    void trackOrder_returnsNullForUnknownOrder() {
        assertNull(orderService.trackOrder("cust1", false, "ORD-UNKNOWN"));
    }

    // ── query methods ────────────────────────────────────────────────────────

    @Test
    void getOrdersByCustomer_returnsOnlyThatCustomersOrders() {
        cartService.addItem("cust1", "P1", 1);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertEquals(1, orderService.getOrdersByCustomer("cust1").size());
        assertEquals(0, orderService.getOrdersByCustomer("cust2").size());
    }

    @Test
    void getOrdersByStatus_filtersCorrectly() {
        cartService.addItem("cust1", "P1", 1);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");
        assertEquals(1, orderService.getOrdersByStatus(OrderStatus.CONFIRMED).size());
        assertEquals(0, orderService.getOrdersByStatus(OrderStatus.SHIPPED).size());
    }
}
