package eu.ageekatyourservice.vadinvoicing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column
    private String website;

    @Column
    private String contact;

    @Column(precision = 10, scale = 2)
    private BigDecimal billingRate;

    @Column
    private String phoneNumber;

    @Column
    private String email;
}
