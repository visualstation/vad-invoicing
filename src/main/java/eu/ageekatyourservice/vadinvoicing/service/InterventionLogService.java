package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterventionLogService {
    
    @Autowired
    private InterventionLogRepository repository;
    
    public List<InterventionLog> getAllLogs() {
        return repository.findAll();
    }
    
    public InterventionLog saveLog(InterventionLog log) {
        return repository.save(log);
    }
    
    public void deleteLog(Long id) {
        repository.deleteById(id);
    }
    
    public List<InterventionLog> searchByUsername(String username) {
        return repository.findByUsernameContaining(username);
    }
    
    public List<InterventionLog> searchByDescription(String description) {
        return repository.findByDescriptionContaining(description);
    }
}
