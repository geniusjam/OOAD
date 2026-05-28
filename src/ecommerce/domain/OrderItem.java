package ecommerce.domain;

public class OrderItem {
    private String productId;
    private int quantity;
    private double unitPrice;

    public OrderItem(String productId, int quantity, double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return unitPrice * quantity; }
}
