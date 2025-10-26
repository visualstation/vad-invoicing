package eu.ageekatyourservice.vadinvoicing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Id
    @Min(value = 100000000L, message = "Device ID must be at least 9 digits")
    @Max(value = 9999999999L, message = "Device ID cannot exceed 10 digits")
    private Long id;

    @Column(length = 255)
    @Size(max = 255, message = "Label cannot exceed 255 characters")
    private String label;

    @Column(length = 255)
    @Size(max = 255, message = "Alias cannot exceed 255 characters")
    private String alias;

    @Column(name = "comment_text", length = 2000)
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    private String comment;

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
