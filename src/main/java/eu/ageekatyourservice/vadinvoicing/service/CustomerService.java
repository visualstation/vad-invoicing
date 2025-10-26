package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.model.Customer;
import eu.ageekatyourservice.vadinvoicing.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    
    private final CustomerRepository repository;
    
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }
    
    public List<Customer> findAllCustomers() {
        return repository.findAll();
    }
    
    public Optional<Customer> findCustomerById(Long id) {
        return repository.findById(id);
    }
    
    public Customer saveCustomer(Customer customer) {
        return repository.save(customer);
    }
    
    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }
    
    public List<Customer> searchCustomers(String name) {
        return repository.findByNameContaining(name);
    }
    
    public List<Customer> searchByEmail(String email) {
        return repository.findByEmailContaining(email);
    }
}
