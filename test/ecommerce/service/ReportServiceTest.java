package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.Product;
import ecommerce.infrastructure.InMemoryOrderRepository;
import ecommerce.infrastructure.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTest {

    private ReportService reportService;
    private OrderService orderService;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        InMemoryProductRepository productRepo = new InMemoryProductRepository();
        productRepo.save(new Product("P1", "Widget", "desc", "Tools", 10.0, 50));
        ProductService productService = new ProductService(productRepo);
        cartService = new CartService(productService);
        orderService = new OrderService(cartService, productService, new PaymentService(), new InMemoryOrderRepository());
        reportService = new ReportService(orderService);
    }

    @Test
    void filterByStatus_returnsMatchingOrders() {
        cartService.addItem("cust1", "P1", 1);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");

        List<Order> confirmed = reportService.filterByStatus(OrderStatus.CONFIRMED);
        assertEquals(1, confirmed.size());
        assertEquals(OrderStatus.CONFIRMED, confirmed.get(0).getStatus());
    }

    @Test
    void filterByStatus_returnsEmptyWhenNoMatch() {
        assertTrue(reportService.filterByStatus(OrderStatus.SHIPPED).isEmpty());
    }

    @Test
    void filterByDateRange_includesOrdersInRange() {
        cartService.addItem("cust1", "P1", 1);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");

        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        LocalDateTime to = LocalDateTime.now().plusMinutes(1);
        assertEquals(1, reportService.filterByDateRange(from, to).size());
    }

    @Test
    void filterByDateRange_excludesOrdersOutsideRange() {
        cartService.addItem("cust1", "P1", 1);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");

        LocalDateTime from = LocalDateTime.now().plusHours(1);
        LocalDateTime to = LocalDateTime.now().plusHours(2);
        assertTrue(reportService.filterByDateRange(from, to).isEmpty());
    }

    @Test
    void filterByDateRange_isInclusiveOnBoundaries() {
        cartService.addItem("cust1", "P1", 1);
        orderService.placeOrder("cust1", PaymentMethod.MASTERCARD, "details");

        // Grab the order's actual timestamp and use it as both boundaries
        Order order = orderService.getAllOrders().get(0);
        LocalDateTime ts = order.getTimestamp();
        assertEquals(1, reportService.filterByDateRange(ts, ts).size());
    }
}
