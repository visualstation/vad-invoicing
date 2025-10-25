package eu.ageekatyourservice.vadinvoicing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    @Column(nullable = false)
    private LocalDateTime commentDate;
    
    @Column(nullable = false, length = 2000)
    private String comment;
    
    private String author;
    
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}
