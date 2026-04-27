package backend.repositories;

import backend.models.Medicine;
import java.util.List;
import java.util.Optional;

// Repository interface for managing medicines in the pharmacy inventory system
public interface MedicineRepository {
    void add(Medicine medicine);
    void update(Medicine medicine);
    void delete(int id);
    Optional<Medicine> findById(int id);
    Optional<Medicine> findByName(String name);
    List<Medicine> findAll();
    List<Medicine> search(String keyword);
}