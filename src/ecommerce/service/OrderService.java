package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;
import ecommerce.infrastructure.OrderRepository;

import java.util.List;

public class OrderService {
    private final CartService cartService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;

    public OrderService(CartService cart, ProductService prod, PaymentService pay,
                        NotificationService notif, OrderRepository repo) {
        this.cartService = cart;
        this.productService = prod;
        this.paymentService = pay;
        this.notificationService = notif;
        this.orderRepository = repo;
    }

    public String placeOrder(String customerId, PaymentMethod method, String paymentDetails) {
        Cart cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty.");
            return null;
        }

        boolean reserved = productService.reduceStock(cart.getItems());
        if (!reserved) {
            System.out.println("One or more items are out of stock. Order cannot be placed.");
            return null;
        }

        Order order = new Order("ORD-" + System.currentTimeMillis(), customerId, cart.getItems());
        orderRepository.save(order);

        PaymentStatus paymentStatus = paymentService.processPayment(order, method, paymentDetails);

        if (paymentStatus == PaymentStatus.APPROVED) {
            order.setStatus(OrderStatus.CONFIRMED);
            cartService.clearCart(customerId);
            notificationService.notifyCustomer(customerId, "Your order " + order.getOrderId() + " is CONFIRMED.");
        } else {
            productService.restoreStock(order.getItems());
            order.setStatus(OrderStatus.CANCELLED);
            notificationService.notifyCustomer(customerId, "Your order was CANCELLED (payment declined).");
        }

        return order.getOrderId();
    }

    public void cancelOrder(String customerId, String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            return;
        }
        if (!order.getCustomerId().equals(customerId)) {
            System.out.println("You do not own this order.");
            return;
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            System.out.println("Order cannot be cancelled in status: " + order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.CANCELLED);
        productService.restoreStock(order.getItems());
        notificationService.notifyCustomer(customerId, "Your order " + orderId + " has been CANCELLED.");
    }

    public Order trackOrder(String requesterId, boolean isAdmin, String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            return null;
        }
        if (!isAdmin && !order.getCustomerId().equals(requesterId)) {
            System.out.println("Access denied: you do not own this order.");
            return null;
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
