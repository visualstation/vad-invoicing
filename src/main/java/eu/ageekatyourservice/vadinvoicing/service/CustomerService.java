package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository repository;
    
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }
    
    public Optional<Customer> getCustomerById(Long id) {
        return repository.findById(id);
    }
    
    public Customer saveCustomer(Customer customer) {
        return repository.save(customer);
    }
    
    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }
    
    public List<Customer> searchByName(String name) {
        return repository.findByNameContaining(name);
    }
    
    public List<Customer> searchByEmail(String email) {
        return repository.findByEmailContaining(email);
    }
}
