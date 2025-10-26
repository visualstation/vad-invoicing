package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> all() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer one(@PathVariable Long id) {
        return customerService.getCustomerById(id).orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        Customer saved = customerService.saveCustomer(customer);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        Customer existing = customerService.getCustomerById(id).orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
        customer.setId(existing.getId());
        return customerService.saveCustomer(customer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.getCustomerById(id).orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
        customerService.deleteCustomer(id);
    }
}
