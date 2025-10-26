package eu.ageekatyourservice.vadinvoicing.api;

import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.service.InterventionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervention-logs")
public class InterventionLogRestController {

    @Autowired
    private InterventionLogService interventionLogService;

    @GetMapping
    public ResponseEntity<List<InterventionLog>> getAllLogs() {
        return ResponseEntity.ok(interventionLogService.getAllLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionLog> getLogById(@PathVariable Long id) {
        return interventionLogService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InterventionLog> createLog(@RequestBody InterventionLog log) {
        InterventionLog saved = interventionLogService.saveLog(log);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionLog> updateLog(@PathVariable Long id, @RequestBody InterventionLog log) {
        if (!interventionLogService.getLogById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        log.setId(id);
        InterventionLog updated = interventionLogService.saveLog(log);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        if (!interventionLogService.getLogById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        interventionLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
