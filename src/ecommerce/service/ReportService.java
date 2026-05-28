package ecommerce.service;

import ecommerce.domain.Order;
import ecommerce.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private final OrderService orderService;

    public ReportService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<Order> filterByStatus(OrderStatus status) {
        return orderService.getAllOrders().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Order> filterByDateRange(LocalDateTime from, LocalDateTime to) {
        return orderService.getAllOrders().stream()
                .filter(o -> !o.getTimestamp().isBefore(from) && !o.getTimestamp().isAfter(to))
                .collect(Collectors.toList());
    }

    public void printReport(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders match the filter.");
            return;
        }
        for (Order o : orders) {
            System.out.println(o.getOrderId() + " | " + o.getCustomerId()
                    + " | " + o.getStatus() + " | " + o.getTimestamp());
        }
    }
}
