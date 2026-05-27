package ecommerce;

import ecommerce.domain.Product;
import ecommerce.infrastructure.InMemoryOrderRepository;
import ecommerce.infrastructure.OrderRepository;
import ecommerce.presentation.ConsoleUI;
import ecommerce.service.CartService;
import ecommerce.service.NotificationService;
import ecommerce.service.OrderService;
import ecommerce.service.PaymentService;
import ecommerce.service.ProductService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        OrderRepository orderRepository = new InMemoryOrderRepository();
        CartService cartService = new CartService();
        ProductService productService = new ProductService();
        PaymentService paymentService = new PaymentService();
        NotificationService notificationService = new NotificationService();

        OrderService orderService = new OrderService(
                cartService,
                productService,
                paymentService,
                notificationService,
                orderRepository
        );

        // Seed catalog with 10 products (product1..product10), stock = 10 each.
        // We keep a parallel List<Product> because ProductService doesn't
        // expose a "list all products" method and we can't modify it.
        List<Product> catalog = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product p = new Product("P" + i, "product" + i, 10);
            productService.register(p);
            catalog.add(p);
        }

        ConsoleUI ui = new ConsoleUI(
                orderService,
                paymentService,
                cartService,
                productService,
                catalog
        );
        ui.start();
    }
}