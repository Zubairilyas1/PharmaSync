package frontend.pages;

import java.time.LocalDate;
import java.util.*;

/**
 * Global customer and transaction database for PharmaSync
 * Stores customer information and their purchase/return history
 */
public class CustomerDatabase {
    
    // Customer data structure
    public static class Customer {
        public String customerId;
        public String customerName;
        public LocalDate registrationDate;
        public List<Transaction> transactionHistory;
        
        public Customer(String customerId, String customerName) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.registrationDate = LocalDate.now();
            this.transactionHistory = new ArrayList<>();
        }
    }
    
    // Transaction record
    public static class Transaction {
        public String transactionId;
        public LocalDate date;
        public String medicineName;
        public String batchId;
        public int quantity;
        public double pricePerUnit;
        public double totalAmount;
        public String type; // "SALE" or "RETURN"
        public String returnCondition; // "Wrong Item (Restock)" or "Damaged/Faulty (Quarantine)" - null for sales
        
        public Transaction(String transactionId, LocalDate date, String medicineName, String batchId, 
                          int quantity, double pricePerUnit, String type) {
            this.transactionId = transactionId;
            this.date = date;
            this.medicineName = medicineName;
            this.batchId = batchId;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
            this.totalAmount = quantity * pricePerUnit;
            this.type = type;
        }
    }
    
    // Global storage
    private static Map<String, Customer> customers = new HashMap<>();
    private static int customerIdCounter = 1000;
    private static int transactionIdCounter = 5000;
    
    /**
     * Register or get existing customer by name
     */
    public static Customer getOrCreateCustomer(String customerName) {
        // Check if customer already exists
        for (Customer customer : customers.values()) {
            if (customer.customerName.equalsIgnoreCase(customerName)) {
                return customer;
            }
        }
        
        // Create new customer
        String customerId = "CUST-" + (customerIdCounter++);
        Customer newCustomer = new Customer(customerId, customerName);
        customers.put(customerId, newCustomer);
        System.out.println("✓ New customer registered: " + customerName + " (" + customerId + ")");
        return newCustomer;
    }
    
    /**
     * Get customer by name
     */
    public static Customer getCustomerByName(String customerName) {
        for (Customer customer : customers.values()) {
            if (customer.customerName.equalsIgnoreCase(customerName)) {
                return customer;
            }
        }
        return null;
    }
    
    /**
     * Get customer by ID
     */
    public static Customer getCustomerById(String customerId) {
        return customers.get(customerId);
    }
    
    /**
     * Add sale transaction to customer history
     */
    public static String addSaleTransaction(String customerId, String medicineName, String batchId, 
                                            int quantity, double pricePerUnit) {
        Customer customer = customers.get(customerId);
        if (customer == null) return null;
        
        String transactionId = "TXN-" + (transactionIdCounter++);
        Transaction transaction = new Transaction(transactionId, LocalDate.now(), medicineName, 
                                                 batchId, quantity, pricePerUnit, "SALE");
        customer.transactionHistory.add(transaction);
        
        System.out.println("✓ Sale recorded for " + customer.customerName + ": " + medicineName + 
                          " x" + quantity + " (" + transactionId + ")");
        return transactionId;
    }
    
    /**
     * Add return transaction to customer history
     */
    public static String addReturnTransaction(String customerId, String medicineName, String batchId,
                                             int quantity, double pricePerUnit, String returnCondition) {
        Customer customer = customers.get(customerId);
        if (customer == null) return null;
        
        String transactionId = "RET-" + (transactionIdCounter++);
        Transaction transaction = new Transaction(transactionId, LocalDate.now(), medicineName, 
                                                 batchId, quantity, pricePerUnit, "RETURN");
        transaction.returnCondition = returnCondition;
        customer.transactionHistory.add(transaction);
        
        System.out.println("✓ Return recorded for " + customer.customerName + ": " + medicineName + 
                          " x" + quantity + " (" + returnCondition + ")");
        return transactionId;
    }
    
    /**
     * Get all customers
     */
    public static List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }
    
    /**
     * Get customer transaction history
     */
    public static List<Transaction> getCustomerTransactionHistory(String customerId) {
        Customer customer = customers.get(customerId);
        return customer != null ? customer.transactionHistory : new ArrayList<>();
    }
    
    /**
     * Clear all data (for testing)
     */
    public static void clearAll() {
        customers.clear();
        customerIdCounter = 1000;
        transactionIdCounter = 5000;
    }
    
    /**
     * Get statistics
     */
    public static Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customers.size());
        
        int totalTransactions = 0;
        double totalRevenue = 0;
        for (Customer customer : customers.values()) {
            totalTransactions += customer.transactionHistory.size();
            for (Transaction txn : customer.transactionHistory) {
                if ("SALE".equals(txn.type)) {
                    totalRevenue += txn.totalAmount;
                }
            }
        }
        
        stats.put("totalTransactions", totalTransactions);
        stats.put("totalRevenue", totalRevenue);
        return stats;
    }
}
