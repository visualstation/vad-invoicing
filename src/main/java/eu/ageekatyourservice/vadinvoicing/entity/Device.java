package eu.ageekatyourservice.vadinvoicing.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Device {

    // Primary key: 9-10 digit numeric id, unique
    @Id
    @Min(100000000L)
    @Max(9999999999L)
    private Long id;

    // Optional human-friendly label
    @Column(length = 255)
    @Size(max = 255)
    private String label;

    // Optional alias
    @Column(length = 255)
    @Size(max = 255)
    private String alias;

    // Optional free-text comment. Use a safe column name across DBs
    @Column(name = "comment_text", length = 2000)
    @Size(max = 2000)
    private String comment;

    // Device may or may not be linked to a customer
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<DeviceComment> comments = new LinkedHashSet<>();
}
