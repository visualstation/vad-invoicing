package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import eu.ageekatyourservice.vadinvoicing.service.LogFilterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class InterventionLogController {

    private final InterventionLogRepository repository;
    private final LogFilterService logFilterService;

    public InterventionLogController(InterventionLogRepository repository, LogFilterService logFilterService) {
        this.repository = repository;
        this.logFilterService = logFilterService;
    }

    @GetMapping
    public Page<InterventionLog> all(
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        if (customerId != null) {
            return logFilterService.getLogsFilteredByCustomerId(customerId, pageable);
        }
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public InterventionLog one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<InterventionLog> create(@Valid @RequestBody InterventionLog log) {
        InterventionLog saved = repository.save(log);
        return ResponseEntity.created(URI.create("/api/logs/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public InterventionLog update(@PathVariable Long id, @Valid @RequestBody InterventionLog log) {
        InterventionLog existing = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log " + id + " not found"));
        log.setId(existing.getId());
        return repository.save(log);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @GetMapping("/search")
    public List<InterventionLog> search(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return repository.findByTimestampBetween(start, end);
    }
}
