package eu.ageekatyourservice.vadinvoicing.api;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceRestController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        return deviceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        Device saved = deviceService.save(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        if (!deviceService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        device.setId(id);
        Device updated = deviceService.save(device);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        if (!deviceService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
