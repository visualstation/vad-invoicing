package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.model.Device;
import eu.ageekatyourservice.vadinvoicing.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Devices", description = "Device management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DeviceController {
    
    private final DeviceService deviceService;
    
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
    
    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieve a list of all devices")
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.findAllDevices());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get device by ID", description = "Retrieve a device by its ID")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.findDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "id", id));
        return ResponseEntity.ok(device);
    }
    
    @PostMapping
    @Operation(summary = "Create device", description = "Create a new device")
    public ResponseEntity<Device> createDevice(@Valid @RequestBody Device device) {
        Device createdDevice = deviceService.saveDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update device", description = "Update an existing device")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, 
                                               @Valid @RequestBody Device deviceDetails) {
        Device device = deviceService.findDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "id", id));
        
        device.setLabel(deviceDetails.getLabel());
        device.setAlias(deviceDetails.getAlias());
        device.setComment(deviceDetails.getComment());
        device.setCustomer(deviceDetails.getCustomer());
        
        Device updatedDevice = deviceService.saveDevice(device);
        return ResponseEntity.ok(updatedDevice);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete device", description = "Delete a device by its ID")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        Device device = deviceService.findDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "id", id));
        deviceService.deleteDevice(device.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get devices by customer", description = "Retrieve devices for a specific customer")
    public ResponseEntity<List<Device>> getDevicesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(deviceService.findByCustomerId(customerId));
    }
}
