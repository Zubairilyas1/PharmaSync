package backend.services;

import backend.exceptions.InventoryException;
import backend.models.Medicine;
import backend.repositories.MedicineRepository;
import java.util.List;
import java.util.Optional;

// Service class for managing medicine inventory
public class InventoryService {
    private final MedicineRepository medicineRepository;

    public InventoryService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    // Adds a new medicine to the inventory after validating inputs and checking for duplicates
    public void addMedicine(backend.models.Medicine medicine) throws InventoryException {
        validateMedicineInputs(medicine.getName(), medicine.getPrice(), medicine.getStockQuantity());
        if (medicineRepository.findByName(medicine.getName()).isPresent()) {
            throw new InventoryException("A medicine with the name '" + medicine.getName() + "' already exists.");
        }
        medicineRepository.add(medicine);
    }

    // Updates an existing medicine in the inventory after validating inputs and checking for duplicates
    public void updateMedicine(backend.models.Medicine medicine) throws InventoryException {
        validateMedicineInputs(medicine.getName(), medicine.getPrice(), medicine.getStockQuantity());
        Optional<Medicine> existing = medicineRepository.findById(medicine.getId());
        if (existing.isEmpty()) {
            throw new InventoryException("Medicine not found.");
        }
        Optional<Medicine> byName = medicineRepository.findByName(medicine.getName());
        if (byName.isPresent() && byName.get().getId() != medicine.getId()) {
            throw new InventoryException("Another medicine with the name '" + medicine.getName() + "' already exists.");
        }
        medicineRepository.update(medicine);
    }

    // Removes a medicine from the inventory by its ID after checking if it exists
    public void removeMedicine(int id) throws InventoryException {
        if (medicineRepository.findById(id).isEmpty()) {
            throw new InventoryException("Medicine not found.");
        }
        medicineRepository.delete(id);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public List<Medicine> searchMedicines(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMedicines();
        }
        return medicineRepository.search(keyword);
    }

    public Medicine getMedicineById(int id) throws InventoryException {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new InventoryException("Medicine not found."));
    }

    public Medicine getMedicineByName(String name) throws InventoryException {
         return medicineRepository.findByName(name)
                .orElseThrow(() -> new InventoryException("Medicine not found."));
    }

    public void processSale(int medicineId, int quantityToSell) throws InventoryException {
        if (quantityToSell <= 0) {
             throw new InventoryException("Sale quantity must be greater than zero.");
        }
        
        Medicine med = getMedicineById(medicineId);
        
        if (med.getStockQuantity() < quantityToSell) {
             throw new InventoryException("Insufficient stock for " + med.getName() + ". Available: " + med.getStockQuantity());
        }
        
        med.setStockQuantity(med.getStockQuantity() - quantityToSell);
        medicineRepository.update(med);
    }

    // Validates the inputs for a medicine, ensuring that the name is not empty and that price and stock are non-negative
    private void validateMedicineInputs(String name, double price, int stock) throws InventoryException {
        if (name == null || name.trim().isEmpty()) {
            throw new InventoryException("Medicine name cannot be empty.");
        }
        if (price < 0) {
            throw new InventoryException("Price cannot be negative.");
        }
        if (stock < 0) {
            throw new InventoryException("Stock quantity cannot be negative.");
        }
    }
}