package ecommerce.domain;

public class Product {
    private String productId;
    private String name;
    private int stockQuantity;

    public Product(String productId, String name, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() { return productId; }
    public int getStockQuantity() { return stockQuantity; }
    public void reduceStock(int amount) { this.stockQuantity -= amount; }
    public void restoreStock(int amount) { this.stockQuantity += amount; }
}
