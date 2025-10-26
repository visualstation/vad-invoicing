package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.model.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterventionLogService {
    
    private final InterventionLogRepository repository;

    public InterventionLogService(InterventionLogRepository repository) {
        this.repository = repository;
    }
    
    public List<InterventionLog> findAllInterventionLogs() {
        return repository.findAll();
    }

    public Optional<InterventionLog> findInterventionLogById(Long id) {
        return repository.findById(id);
    }
    
    public InterventionLog saveInterventionLog(InterventionLog log) {
        return repository.save(log);
    }
    
    public void deleteInterventionLog(Long id) {
        repository.deleteById(id);
    }
    
    public List<InterventionLog> findByClientId(String clientId) {
        return repository.findByClientId(clientId);
    }
    
    public List<InterventionLog> searchByUsername(String username) {
        return repository.findByUsernameContaining(username);
    }
    
    public List<InterventionLog> searchByDescription(String description) {
        return repository.findByDescriptionContaining(description);
    }
}
