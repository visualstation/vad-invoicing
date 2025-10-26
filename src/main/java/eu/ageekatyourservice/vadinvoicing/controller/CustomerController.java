package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.model.Customer;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {
    
    private final CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by their ID")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping
    @Operation(summary = "Create customer", description = "Create a new customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, 
                                                   @Valid @RequestBody Customer customerDetails) {
        Customer customer = customerService.findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        customer.setName(customerDetails.getName());
        customer.setAddress(customerDetails.getAddress());
        customer.setWebsite(customerDetails.getWebsite());
        customer.setContact(customerDetails.getContact());
        customer.setBillingRate(customerDetails.getBillingRate());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        customer.setEmail(customerDetails.getEmail());
        
        Customer updatedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer by their ID")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        Customer customer = customerService.findCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customerService.deleteCustomer(customer.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Search customers by name")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String name) {
        return ResponseEntity.ok(customerService.searchCustomers(name));
    }
}
