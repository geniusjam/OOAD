package ecommerce.presentation;

import ecommerce.domain.Cart;
import ecommerce.domain.Order;
import ecommerce.domain.OrderItem;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;
import ecommerce.domain.Product;
import ecommerce.service.CartService;
import ecommerce.service.OrderService;
import ecommerce.service.PaymentService;
import ecommerce.service.ProductService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final CartService cartService;
    private final ProductService productService;
    private final List<Product> catalog;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(OrderService orderService,
                     PaymentService paymentService,
                     CartService cartService,
                     ProductService productService,
                     List<Product> catalog) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.cartService = cartService;
        this.productService = productService;
        this.catalog = catalog;
    }

    public void start() {
        System.out.println("--- E-Commerce Order Processing System ---");
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> adminMenu();
                case "2" -> customerLogin();
                case "3" -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ---------- Admin ----------

    private void adminMenu() {
        while (true) {
            System.out.println("\n-- Admin Menu --");
            System.out.println("1. Add new product");
            System.out.println("2. List products");
            System.out.println("3. Back to main menu");
            System.out.print("Choose: ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1" -> addProduct();
                case "2" -> listProducts();
                case "3" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addProduct() {
        System.out.print("Product ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Product name: ");
        String name = scanner.nextLine().trim();
        int qty = readPositiveInt("Stock quantity: ");
        Product p = new Product(id, name, qty);
        productService.register(p);
        catalog.add(p);
        System.out.println("Added product: " + name + " (id=" + id + ", stock=" + qty + ")");
    }

    private void listProducts() {
        if (catalog.isEmpty()) {
            System.out.println("Catalog is empty.");
            return;
        }
        System.out.println("Available products:");
        for (int i = 0; i < catalog.size(); i++) {
            Product p = catalog.get(i);
            System.out.printf("  %d) id=%s, stock=%d%n",
                    i + 1, p.getProductId(), p.getStockQuantity());
        }
    }

    // ---------- Customer ----------

    private void customerLogin() {
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine().trim();
        if (customerId.isEmpty()) {
            System.out.println("Customer ID cannot be empty.");
            return;
        }
        customerMenu(customerId);
    }

    private void customerMenu(String customerId) {
        while (true) {
            System.out.println("\n-- Customer Menu (" + customerId + ") --");
            System.out.println("1. Add item to cart");
            System.out.println("2. Show cart");
            System.out.println("3. Pay");
            System.out.println("4. Exit (back to main menu)");
            System.out.print("Choose: ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1" -> addToCart(customerId);
                case "2" -> showCart(customerId);
                case "3" -> pay(customerId);
                case "4" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addToCart(String customerId) {
        listProducts();
        if (catalog.isEmpty()) return;
        int idx = readPositiveInt("Pick a product number: ");
        if (idx < 1 || idx > catalog.size()) {
            System.out.println("Invalid product number.");
            return;
        }
        Product p = catalog.get(idx - 1);
        int qty = readPositiveInt("Quantity: ");
        if (qty > p.getStockQuantity()) {
            System.out.println("Not enough stock. Available: " + p.getStockQuantity());
            return;
        }
        Cart cart = cartService.getCart(customerId);
        cart.addItem(new OrderItem(p.getProductId(), qty));
        p.reduceStock(qty);
        System.out.println("Added " + qty + " x " + p.getProductId()
                + " to cart. (remaining stock: " + p.getStockQuantity() + ")");
    }

    private void showCart(String customerId) {
        Cart cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("Cart for " + customerId + ":");
        for (OrderItem item : cart.getItems()) {
            System.out.println("  - " + item.getProductId() + " x " + item.getQuantity());
        }
    }

    private void pay(String customerId) {
        Cart cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty. Add items before paying.");
            return;
        }

        Order order = new Order(
                "ORD-" + System.currentTimeMillis(),
                customerId,
                cart.getItems()
        );

        // Lifecycle messages — printed around the PaymentService call so the
        // user sees each stage the payment goes through.
        System.out.println("[Payment] Status: INITIATED");
        System.out.println("[Payment] Status: VALIDATING");
        System.out.println("[Payment] Status: PROCESSING");

        PaymentStatus result = paymentService.processPayment(
                order,
                PaymentMethod.MASTERCARD,
                "1234-5678-9012"
        );

        System.out.println("[Payment] Status: " + result);

        if (result == PaymentStatus.APPROVED) {
            order.setStatus(OrderStatus.CONFIRMED);
            cart.clear();
            System.out.println("Order " + order.getOrderId() + " CONFIRMED for customer " + customerId + ".");
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            System.out.println("Order " + order.getOrderId() + " CANCELLED (payment declined).");
        }
    }

    // ---------- Helpers ----------

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n > 0) return n;
            } catch (NumberFormatException ignored) {
                // fall through
            }
            System.out.println("Please enter a positive integer.");
        }
    }
}