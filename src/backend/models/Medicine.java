package backend.models;

import java.time.LocalDate;

// Represents a medicine in the pharmacy inventory system
public class Medicine {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String batchId;
    private LocalDate expiryDate;
    private String status;

    public Medicine(int id, String name, String description, double price, int stockQuantity, String batchId, LocalDate expiryDate, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.batchId = batchId;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public Medicine(int id, String name, String description, double price, int stockQuantity) {
        this(id, name, description, price, stockQuantity, "UNKNOWN", LocalDate.now().plusYears(1), "Active");
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public int getStockLevel() { return stockQuantity; }
    public String getBatchId() { return batchId; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setStockLevel(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setStatus(String status) { this.status = status; }
}