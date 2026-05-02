package backend.models;

import java.time.LocalDateTime;

public class Sale {
    private int id;
    private int medicineId;
    private int quantity;
    private double totalPrice;
    private LocalDateTime timestamp;
    private String completedBy;

    public Sale(int id, int medicineId, int quantity, double totalPrice, LocalDateTime timestamp, String completedBy) {
        this.id = id;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
        this.completedBy = completedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
}