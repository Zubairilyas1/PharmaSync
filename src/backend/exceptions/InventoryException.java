package backend.exceptions;

// Custom exception for inventory-related errors
public class InventoryException extends Exception {
    public InventoryException(String message) {
        super(message);
    }
}