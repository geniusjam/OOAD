package ecommerce.domain;

public class Product {
    private String productId;
    private String name;
    private String description;
    private String category;
    private double price;
    private int stockQuantity;

    public Product(String productId, String name, String description, String category, double price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void reduceStock(int amount) { this.stockQuantity -= amount; }
    public void restoreStock(int amount) { this.stockQuantity += amount; }
}
