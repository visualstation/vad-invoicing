package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import eu.ageekatyourservice.vadinvoicing.service.DeviceCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/device-comments")
public class DeviceCommentController {

    private final DeviceCommentService deviceCommentService;
    private final DeviceRepository deviceRepository;

    public DeviceCommentController(DeviceCommentService deviceCommentService, DeviceRepository deviceRepository) {
        this.deviceCommentService = deviceCommentService;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping
    public List<DeviceComment> all() { return deviceCommentService.findAll(); }

    @GetMapping("/device/{deviceId}")
    public List<DeviceComment> listByDevice(@PathVariable Long deviceId) {
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new ResourceNotFoundException("Device " + deviceId + " not found"));
        return deviceCommentService.findByDevice(device);
    }

    @GetMapping("/{id}")
    public DeviceComment one(@PathVariable Long id) {
        return deviceCommentService.findById(id);
    }

    @PostMapping
    public ResponseEntity<DeviceComment> create(@Valid @RequestBody DeviceComment comment) {
        DeviceComment saved = deviceCommentService.save(comment);
        return ResponseEntity.created(URI.create("/api/device-comments/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public DeviceComment update(@PathVariable Long id, @Valid @RequestBody DeviceComment comment) {
        DeviceComment existing = deviceCommentService.findById(id);
        comment.setId(existing.getId());
        return deviceCommentService.save(comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deviceCommentService.delete(id);
    }
}
