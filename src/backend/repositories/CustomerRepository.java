package backend.repositories;

import backend.models.Customer;
import java.util.List;

public interface CustomerRepository {
    void add(Customer customer);
    void update(Customer customer);
    void delete(int id);
    Customer findById(int id);
    List<Customer> findAll();
}