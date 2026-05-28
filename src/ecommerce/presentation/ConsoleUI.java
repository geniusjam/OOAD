package ecommerce.presentation;

import ecommerce.domain.CartItem;
import ecommerce.domain.Order;
import ecommerce.domain.OrderItem;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.Product;
import ecommerce.domain.User;
import ecommerce.domain.UserRole;
import ecommerce.service.AuthService;
import ecommerce.service.CartService;
import ecommerce.service.OrderService;
import ecommerce.service.ProductService;
import ecommerce.service.ReportService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;
    private final ReportService reportService;
    private final AuthService authService;
    private final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleUI(OrderService orderService, CartService cartService,
                     ProductService productService, ReportService reportService,
                     AuthService authService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.productService = productService;
        this.reportService = reportService;
        this.authService = authService;
    }

    public void start() {
        System.out.println("--- E-Commerce Order Processing System ---");
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> loginFlow();
                case "2" -> registerFlow();
                case "3" -> { System.out.println("Goodbye."); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void loginFlow() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = authService.login(username, password);
        if (user == null) return;
        System.out.println("Welcome, " + user.getUsername() + "!");
        if (user.getRole() == UserRole.ADMIN) {
            adminMenu();
        } else {
            customerMenu(user.getUserId());
        }
    }

    private void registerFlow() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = authService.register(username, email, password, UserRole.CUSTOMER);
        if (user != null) {
            System.out.println("Registration successful. You can now log in.");
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n-- Admin Menu --");
            System.out.println("1. Add product");
            System.out.println("2. Remove product");
            System.out.println("3. Modify product");
            System.out.println("4. List all products");
            System.out.println("5. Search products");
            System.out.println("6. Generate order report");
            System.out.println("7. Manage order status");
            System.out.println("8. Back");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> addProduct();
                case "2" -> removeProduct();
                case "3" -> modifyProduct();
                case "4" -> listProducts(productService.getAllProducts());
                case "5" -> searchProducts();
                case "6" -> adminReport();
                case "7" -> manageOrderStatus();
                case "8" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addProduct() {
        System.out.print("Product ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Category: ");
        String cat = scanner.nextLine().trim();
        double price = readPositiveDouble("Price: ");
        int qty = readPositiveInt("Stock quantity: ");
        productService.register(new Product(id, name, desc, cat, price, qty));
        System.out.println("Product added: " + name);
    }

    private void removeProduct() {
        System.out.print("Product ID to remove: ");
        String id = scanner.nextLine().trim();
        if (productService.getProduct(id) == null) {
            System.out.println("Product not found.");
            return;
        }
        productService.remove(id);
        System.out.println("Product removed.");
    }

    private void modifyProduct() {
        System.out.print("Product ID to modify: ");
        String id = scanner.nextLine().trim();
        Product existing = productService.getProduct(id);
        if (existing == null) {
            System.out.println("Product not found.");
            return;
        }
        System.out.println("Current values shown in brackets. Press Enter to keep.");
        System.out.print("Name [" + existing.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = existing.getName();
        System.out.print("Description [" + existing.getDescription() + "]: ");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) desc = existing.getDescription();
        System.out.print("Category [" + existing.getCategory() + "]: ");
        String cat = scanner.nextLine().trim();
        if (cat.isEmpty()) cat = existing.getCategory();
        System.out.print("Price [" + existing.getPrice() + "]: ");
        String priceInput = scanner.nextLine().trim();
        double price = priceInput.isEmpty() ? existing.getPrice() : Double.parseDouble(priceInput);
        System.out.print("Stock [" + existing.getStockQuantity() + "]: ");
        String stockInput = scanner.nextLine().trim();
        int stock = stockInput.isEmpty() ? existing.getStockQuantity() : Integer.parseInt(stockInput);
        productService.updateProduct(id, name, desc, cat, price, stock);
        System.out.println("Product updated.");
    }

    private void searchProducts() {
        System.out.println("Search by: 1. Name  2. Category");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        System.out.print("Query: ");
        String q = scanner.nextLine().trim();
        List<Product> results = c.equals("1")
                ? productService.searchByName(q)
                : productService.searchByCategory(q);
        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            listProducts(results);
        }
    }

    private void adminReport() {
        System.out.println("Filter by: 1. Status  2. Date range");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        List<Order> orders;
        if (c.equals("1")) {
            OrderStatus status = readOrderStatus();
            if (status == null) return;
            orders = reportService.filterByStatus(status);
        } else {
            LocalDateTime from = readDateTime("From (yyyy-MM-dd HH:mm): ");
            LocalDateTime to = readDateTime("To   (yyyy-MM-dd HH:mm): ");
            if (from == null || to == null) return;
            orders = reportService.filterByDateRange(from, to);
        }
        reportService.printReport(orders);
    }

    private void customerMenu(String customerId) {
        while (true) {
            System.out.println("\n-- Customer Menu (" + customerId + ") --");
            System.out.println("1. Browse / add item to cart");
            System.out.println("2. Remove item from cart");
            System.out.println("3. Update item quantity");
            System.out.println("4. Show cart");
            System.out.println("5. Search products");
            System.out.println("6. Checkout");
            System.out.println("7. Cancel order");
            System.out.println("8. Track order");
            System.out.println("9. Back");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> addToCart(customerId);
                case "2" -> removeFromCart(customerId);
                case "3" -> updateCartQuantity(customerId);
                case "4" -> showCart(customerId);
                case "5" -> searchProducts();
                case "6" -> checkout(customerId);
                case "7" -> cancelOrder(customerId);
                case "8" -> trackOrder(customerId);
                case "9" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addToCart(String customerId) {
        List<Product> all = productService.getAllProducts();
        if (all.isEmpty()) {
            System.out.println("Catalog is empty.");
            return;
        }
        listProducts(all);
        int idx = readPositiveInt("Pick product number: ");
        if (idx < 1 || idx > all.size()) {
            System.out.println("Invalid number.");
            return;
        }
        Product chosen = all.get(idx - 1);
        int qty = readPositiveInt("Quantity: ");
        if (qty > chosen.getStockQuantity()) {
            System.out.println("Not enough stock. Available: " + chosen.getStockQuantity());
            return;
        }
        cartService.addItem(customerId, chosen.getProductId(), qty);
        System.out.println("Added to cart.");
    }

    private void removeFromCart(String customerId) {
        var cart = cartService.getCart(customerId);
        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            System.out.println((i + 1) + ". " + item.getProduct().getName() + " x" + item.getQuantity());
        }
        int idx = readPositiveInt("Pick item to remove: ");
        if (idx < 1 || idx > items.size()) {
            System.out.println("Invalid number.");
            return;
        }
        cartService.removeItem(customerId, items.get(idx - 1).getProduct().getProductId());
        System.out.println("Item removed from cart.");
    }

    private void showCart(String customerId) {
        var cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("Cart for " + customerId + ":");
        double total = 0;
        for (CartItem item : cart.getItems()) {
            total += item.getSubtotal();
            System.out.println("  " + item.getProduct().getName() + " x" + item.getQuantity()
                    + " @ " + item.getProduct().getPrice() + " = " + item.getSubtotal());
        }
        System.out.println("  Total: " + total);
    }

    private void checkout(String customerId) {
        var cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        PaymentMethod method = readPaymentMethod();
        if (method == null) return;
        System.out.print("Card number: ");
        String cardNumber = scanner.nextLine().trim();

        String orderId = orderService.placeOrder(customerId, method, cardNumber);
        if (orderId != null) {
            System.out.println("Order " + orderId + " placed successfully.");
        }
    }

    private void cancelOrder(String customerId) {
        System.out.print("Order ID to cancel: ");
        String orderId = scanner.nextLine().trim();
        if (orderService.cancelOrder(customerId, orderId)) {
            System.out.println("Order " + orderId + " cancelled.");
        }
    }

    private void trackOrder(String customerId) {
        System.out.print("Order ID to track: ");
        String orderId = scanner.nextLine().trim();
        Order order = orderService.trackOrder(customerId, false, orderId);
        if (order != null) printOrderDetails(order);
    }

    private void updateCartQuantity(String customerId) {
        var cart = cartService.getCart(customerId);
        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            System.out.println((i + 1) + ". " + item.getProduct().getName() + " x" + item.getQuantity());
        }
        int idx = readPositiveInt("Pick item to update: ");
        if (idx < 1 || idx > items.size()) {
            System.out.println("Invalid number.");
            return;
        }
        int qty = readPositiveInt("New quantity: ");
        String productId = items.get(idx - 1).getProduct().getProductId();
        if (cartService.updateQuantity(customerId, productId, qty)) {
            System.out.println("Quantity updated.");
        } else {
            System.out.println("Could not update quantity. Check available stock.");
        }
    }

    private void manageOrderStatus() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders.");
            return;
        }
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.println((i + 1) + ". " + o.getOrderId()
                    + " [" + o.getStatus() + "] customer: " + o.getCustomerId());
        }
        int idx = readPositiveInt("Pick order: ");
        if (idx < 1 || idx > orders.size()) {
            System.out.println("Invalid number.");
            return;
        }
        Order order = orders.get(idx - 1);
        System.out.println("Action: 1. Mark SHIPPED  2. Mark DELIVERED");
        System.out.print("Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                if (orderService.shipOrder(order.getOrderId())) {
                    System.out.println("Order marked as SHIPPED.");
                }
            }
            case "2" -> {
                if (orderService.deliverOrder(order.getOrderId())) {
                    System.out.println("Order marked as DELIVERED.");
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void listProducts(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.println((i + 1) + ". [" + p.getProductId() + "] " + p.getName()
                    + " | " + p.getCategory() + " | $" + p.getPrice()
                    + " | stock: " + p.getStockQuantity());
        }
    }

    private void printOrderDetails(Order order) {
        System.out.println("Order ID  : " + order.getOrderId());
        System.out.println("Customer  : " + order.getCustomerId());
        System.out.println("Status    : " + order.getStatus());
        System.out.println("Timestamp : " + order.getTimestamp().format(DT_FMT));
        System.out.println("Items:");
        for (OrderItem item : order.getItems()) {
            System.out.println("  " + item.getProductId() + " x" + item.getQuantity()
                    + " @ " + item.getUnitPrice());
        }
    }

    private PaymentMethod readPaymentMethod() {
        System.out.println("Payment method: 1. MasterCard  2. Visa  3. Troy");
        System.out.print("Choose: ");
        return switch (scanner.nextLine().trim()) {
            case "1" -> PaymentMethod.MASTERCARD;
            case "2" -> PaymentMethod.VISA;
            case "3" -> PaymentMethod.TROY;
            default -> { System.out.println("Invalid payment method."); yield null; }
        };
    }

    private OrderStatus readOrderStatus() {
        System.out.println("Status: 1.PENDING 2.CONFIRMED 3.SHIPPED 4.DELIVERED 5.CANCELLED");
        System.out.print("Choose: ");
        return switch (scanner.nextLine().trim()) {
            case "1" -> OrderStatus.PENDING;
            case "2" -> OrderStatus.CONFIRMED;
            case "3" -> OrderStatus.SHIPPED;
            case "4" -> OrderStatus.DELIVERED;
            case "5" -> OrderStatus.CANCELLED;
            default -> { System.out.println("Invalid status."); yield null; }
        };
    }

    private LocalDateTime readDateTime(String prompt) {
        System.out.print(prompt);
        try {
            return LocalDateTime.parse(scanner.nextLine().trim(), DT_FMT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd HH:mm");
            return null;
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int n = Integer.parseInt(scanner.nextLine().trim());
                if (n > 0) return n;
            } catch (NumberFormatException ignored) {}
            System.out.println("Please enter a positive integer.");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double d = Double.parseDouble(scanner.nextLine().trim());
                if (d > 0) return d;
            } catch (NumberFormatException ignored) {}
            System.out.println("Please enter a positive number.");
        }
    }
}
