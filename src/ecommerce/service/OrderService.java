package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;
import ecommerce.infrastructure.OrderRepository;

public class OrderService {
    private CartService cartService;
    private ProductService productService;
    private PaymentService paymentService;
    private NotificationService notificationService;
    private OrderRepository orderRepository;

    public OrderService(CartService cart, ProductService prod, PaymentService pay, NotificationService notif, OrderRepository repo) {
        this.cartService = cart;
        this.productService = prod;
        this.paymentService = pay;
        this.notificationService = notif;
        this.orderRepository = repo;
    }

    public void placeOrder(String customerId, PaymentMethod method, String paymentDetails) {
        Cart cart = cartService.getCart(customerId);

        Order order = new Order("ORD-" + System.currentTimeMillis(), customerId, cart.getItems());
        orderRepository.save(order);

        PaymentStatus paymentStatus = paymentService.processPayment(order, method, paymentDetails);

        if (paymentStatus == PaymentStatus.APPROVED) {
            order.setStatus(OrderStatus.CONFIRMED);
            cartService.clearCart(customerId);
            notificationService.notifyCustomer(customerId, "Order confirmed");
        } else {
            productService.restoreStock(order.getItems());
            order.setStatus(OrderStatus.CANCELLED);
        }
    }

    public void cancelOrder(String customerId, String orderId) {
        Order order = orderRepository.findById(orderId);

        if (order != null && order.getCustomerId().equals(customerId) &&
           (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED)) {

            order.setStatus(OrderStatus.CANCELLED);
            productService.restoreStock(order.getItems());
            notificationService.notifyCustomer(customerId, "Order cancelled");
        }
    }
}
