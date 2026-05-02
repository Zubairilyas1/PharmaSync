package backend.services;

import backend.models.Customer;
import backend.repositories.CustomerRepository;
import java.util.List;

public class CustomerService {
    private CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public void addCustomer(Customer customer) {
        repository.add(customer);
    }

    public void updateCustomer(Customer customer) {
        repository.update(customer);
    }

    public void deleteCustomer(int id) {
        repository.delete(id);
    }

    public Customer getCustomerById(int id) {
        return repository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }
}