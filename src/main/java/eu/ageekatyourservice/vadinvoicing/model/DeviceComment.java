package eu.ageekatyourservice.vadinvoicing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    @NotNull(message = "Device is required")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Device device;

    @Column(nullable = false, length = 2000)
    @NotBlank(message = "Comment text is required")
    @Size(max = 2000, message = "Comment text cannot exceed 2000 characters")
    private String text;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Created date is required")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
