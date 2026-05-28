package ecommerce.service;

import ecommerce.domain.Cart;
import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;
import ecommerce.domain.PaymentMethod;
import ecommerce.domain.PaymentStatus;
import ecommerce.infrastructure.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final CartService cartService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final List<OrderObserver> observers = new ArrayList<>();

    public OrderService(CartService cart, ProductService prod, PaymentService pay, OrderRepository repo) {
        this.cartService = cart;
        this.productService = prod;
        this.paymentService = pay;
        this.orderRepository = repo;
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(order);
        }
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
            notifyObservers(order);
            cartService.clearCart(customerId);
            return order.getOrderId();
        } else {
            productService.restoreStock(order.getItems());
            order.setStatus(OrderStatus.CANCELLED);
            notifyObservers(order);
            return null;
        }
    }

    public boolean cancelOrder(String customerId, String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            return false;
        }
        if (!order.getCustomerId().equals(customerId)) {
            System.out.println("You do not own this order.");
            return false;
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            System.out.println("Order cannot be cancelled in status: " + order.getStatus());
            return false;
        }
        order.setStatus(OrderStatus.CANCELLED);
        productService.restoreStock(order.getItems());
        notifyObservers(order);
        return true;
    }

    public boolean shipOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            return false;
        }
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            System.out.println("Order must be CONFIRMED to ship. Current status: " + order.getStatus());
            return false;
        }
        order.setStatus(OrderStatus.SHIPPED);
        notifyObservers(order);
        return true;
    }

    public boolean deliverOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            return false;
        }
        if (order.getStatus() != OrderStatus.SHIPPED) {
            System.out.println("Order must be SHIPPED to deliver. Current status: " + order.getStatus());
            return false;
        }
        order.setStatus(OrderStatus.DELIVERED);
        notifyObservers(order);
        return true;
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
