package eu.ageekatyourservice.vadinvoicing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "intervention_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterventionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    @NotBlank(message = "Client ID is required")
    @Size(max = 255, message = "Client ID cannot exceed 255 characters")
    private String clientId;
    
    @Column(nullable = false)
    @NotBlank(message = "Username is required")
    @Size(max = 255, message = "Username cannot exceed 255 characters")
    private String username;
    
    @Column(nullable = false, length = 1000)
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @Column(nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 0, message = "Duration cannot be negative")
    private Integer duration;
    
    @Column(nullable = false)
    @NotNull(message = "Billed duration is required")
    @Min(value = 0, message = "Billed duration cannot be negative")
    private Integer billedDuration;
}
