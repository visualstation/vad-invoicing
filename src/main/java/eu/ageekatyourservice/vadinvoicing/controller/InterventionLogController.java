package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.model.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.service.InterventionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervention-logs")
@Tag(name = "Intervention Logs", description = "Intervention log management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class InterventionLogController {
    
    private final InterventionLogService interventionLogService;
    
    public InterventionLogController(InterventionLogService interventionLogService) {
        this.interventionLogService = interventionLogService;
    }
    
    @GetMapping
    @Operation(summary = "Get all intervention logs", description = "Retrieve a list of all intervention logs")
    public ResponseEntity<List<InterventionLog>> getAllInterventionLogs() {
        return ResponseEntity.ok(interventionLogService.findAllInterventionLogs());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get intervention log by ID", description = "Retrieve an intervention log by its ID")
    public ResponseEntity<InterventionLog> getInterventionLogById(@PathVariable Long id) {
        InterventionLog log = interventionLogService.findInterventionLogById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InterventionLog", "id", id));
        return ResponseEntity.ok(log);
    }
    
    @PostMapping
    @Operation(summary = "Create intervention log", description = "Create a new intervention log")
    public ResponseEntity<InterventionLog> createInterventionLog(@Valid @RequestBody InterventionLog interventionLog) {
        InterventionLog createdLog = interventionLogService.saveInterventionLog(interventionLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update intervention log", description = "Update an existing intervention log")
    public ResponseEntity<InterventionLog> updateInterventionLog(@PathVariable Long id, 
                                                                 @Valid @RequestBody InterventionLog logDetails) {
        InterventionLog log = interventionLogService.findInterventionLogById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InterventionLog", "id", id));
        
        log.setTimestamp(logDetails.getTimestamp());
        log.setClientId(logDetails.getClientId());
        log.setUsername(logDetails.getUsername());
        log.setDescription(logDetails.getDescription());
        log.setDuration(logDetails.getDuration());
        log.setBilledDuration(logDetails.getBilledDuration());
        
        InterventionLog updatedLog = interventionLogService.saveInterventionLog(log);
        return ResponseEntity.ok(updatedLog);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete intervention log", description = "Delete an intervention log by its ID")
    public ResponseEntity<Void> deleteInterventionLog(@PathVariable Long id) {
        InterventionLog log = interventionLogService.findInterventionLogById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InterventionLog", "id", id));
        interventionLogService.deleteInterventionLog(log.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get logs by client", description = "Retrieve intervention logs for a specific client")
    public ResponseEntity<List<InterventionLog>> getLogsByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(interventionLogService.findByClientId(clientId));
    }
}
