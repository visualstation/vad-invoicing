package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public List<Device> all() {
        return deviceService.getAll();
    }

    @GetMapping("/{id}")
    public Device one(@PathVariable Long id) {
        return deviceService.getById(id).orElseThrow(() -> new ResourceNotFoundException("Device " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<Device> create(@Valid @RequestBody Device device) {
        Device saved = deviceService.save(device);
        return ResponseEntity.created(URI.create("/api/devices/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Device update(@PathVariable Long id, @Valid @RequestBody Device device) {
        Device existing = deviceService.getById(id).orElseThrow(() -> new ResourceNotFoundException("Device " + id + " not found"));
        device.setId(existing.getId());
        return deviceService.save(device);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deviceService.getById(id).orElseThrow(() -> new ResourceNotFoundException("Device " + id + " not found"));
        deviceService.delete(id);
    }
}
