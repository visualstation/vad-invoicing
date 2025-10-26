package eu.ageekatyourservice.vadinvoicing.repository;

import eu.ageekatyourservice.vadinvoicing.model.InterventionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterventionLogRepository extends JpaRepository<InterventionLog, Long> {
    
    List<InterventionLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<InterventionLog> findByUsernameContaining(String username);
    
    List<InterventionLog> findByDescriptionContaining(String description);
    
    List<InterventionLog> findByClientId(String clientId);
}
