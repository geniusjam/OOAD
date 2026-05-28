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
import ecommerce.service.ReportService;

public class Main {
    public static void main(String[] args) {
        ProductService productService = new ProductService();
        CartService cartService = new CartService(productService);
        PaymentService paymentService = new PaymentService();
        NotificationService notifService = new NotificationService();
        OrderRepository orderRepository = new InMemoryOrderRepository();

        OrderService orderService = new OrderService(
                cartService, productService, paymentService, orderRepository);
        orderService.addObserver(notifService);

        ReportService reportService = new ReportService(orderService);

        productService.register(new Product("P1", "Laptop", "15-inch laptop", "Electronics", 999.99, 5));
        productService.register(new Product("P2", "Mouse", "Wireless mouse", "Electronics", 29.99, 20));
        productService.register(new Product("P3", "Keyboard", "Mechanical keyboard", "Electronics", 79.99, 15));
        productService.register(new Product("P4", "Monitor", "27-inch monitor", "Electronics", 349.99, 8));
        productService.register(new Product("P5", "Desk", "Standing desk", "Furniture", 499.99, 3));
        productService.register(new Product("P6", "Chair", "Ergonomic chair", "Furniture", 299.99, 6));
        productService.register(new Product("P7", "Notebook", "A5 notebook", "Stationery", 5.99, 50));
        productService.register(new Product("P8", "Pen Set", "Ballpoint pen set", "Stationery", 3.99, 100));
        productService.register(new Product("P9", "Headphones", "Noise cancelling", "Electronics", 199.99, 10));
        productService.register(new Product("P10", "Webcam", "1080p webcam", "Electronics", 89.99, 12));

        new ConsoleUI(orderService, cartService, productService, reportService).start();
    }
}
