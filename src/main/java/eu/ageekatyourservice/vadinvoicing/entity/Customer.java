package eu.ageekatyourservice.vadinvoicing.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"devices"})
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String address;
    
    private String website;
    
    private String contact;
    
    @Column(name = "billing_rate")
    private BigDecimal billingRate;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Device> devices = new LinkedHashSet<>();

    // List of regex rules used to filter logs for this customer
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_rules", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "rule", length = 1000)
    private List<String> rules = new ArrayList<>();
}
