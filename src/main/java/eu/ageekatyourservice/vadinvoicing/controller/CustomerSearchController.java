package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers/search")
public class CustomerSearchController {

    private final CustomerService customerService;

    public CustomerSearchController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
        if (name != null && !name.isBlank()) return customerService.searchByName(name);
        if (email != null && !email.isBlank()) return customerService.searchByEmail(email);
        return customerService.getAllCustomers();
    }
}
