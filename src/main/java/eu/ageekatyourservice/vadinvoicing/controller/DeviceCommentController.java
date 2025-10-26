package eu.ageekatyourservice.vadinvoicing.controller;

import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.model.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.service.DeviceCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-comments")
@Tag(name = "Device Comments", description = "Device comment management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DeviceCommentController {
    
    private final DeviceCommentService deviceCommentService;
    
    public DeviceCommentController(DeviceCommentService deviceCommentService) {
        this.deviceCommentService = deviceCommentService;
    }
    
    @GetMapping
    @Operation(summary = "Get all device comments", description = "Retrieve a list of all device comments")
    public ResponseEntity<List<DeviceComment>> getAllDeviceComments() {
        return ResponseEntity.ok(deviceCommentService.findAllDeviceComments());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get device comment by ID", description = "Retrieve a device comment by its ID")
    public ResponseEntity<DeviceComment> getDeviceCommentById(@PathVariable Long id) {
        DeviceComment comment = deviceCommentService.findDeviceCommentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceComment", "id", id));
        return ResponseEntity.ok(comment);
    }
    
    @PostMapping
    @Operation(summary = "Create device comment", description = "Create a new device comment")
    public ResponseEntity<DeviceComment> createDeviceComment(@Valid @RequestBody DeviceComment deviceComment) {
        DeviceComment createdComment = deviceCommentService.saveDeviceComment(deviceComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update device comment", description = "Update an existing device comment")
    public ResponseEntity<DeviceComment> updateDeviceComment(@PathVariable Long id, 
                                                             @Valid @RequestBody DeviceComment commentDetails) {
        DeviceComment comment = deviceCommentService.findDeviceCommentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceComment", "id", id));
        
        comment.setText(commentDetails.getText());
        comment.setDevice(commentDetails.getDevice());
        
        DeviceComment updatedComment = deviceCommentService.saveDeviceComment(comment);
        return ResponseEntity.ok(updatedComment);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete device comment", description = "Delete a device comment by its ID")
    public ResponseEntity<Void> deleteDeviceComment(@PathVariable Long id) {
        DeviceComment comment = deviceCommentService.findDeviceCommentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceComment", "id", id));
        deviceCommentService.deleteDeviceComment(comment.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "Get comments by device", description = "Retrieve comments for a specific device")
    public ResponseEntity<List<DeviceComment>> getCommentsByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(deviceCommentService.findByDeviceId(deviceId));
    }
}
