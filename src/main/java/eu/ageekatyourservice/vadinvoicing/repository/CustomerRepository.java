package eu.ageekatyourservice.vadinvoicing.repository;

import eu.ageekatyourservice.vadinvoicing.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByNameContaining(String name);
    
    List<Customer> findByEmailContaining(String email);
}
