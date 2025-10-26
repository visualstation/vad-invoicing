package eu.ageekatyourservice.vadinvoicing.api;

import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.service.DeviceCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-comments")
public class DeviceCommentRestController {

    @Autowired
    private DeviceCommentService deviceCommentService;

    @GetMapping
    public ResponseEntity<List<DeviceComment>> getAllComments() {
        return ResponseEntity.ok(deviceCommentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceComment> getCommentById(@PathVariable Long id) {
        return deviceCommentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DeviceComment> createComment(@RequestBody DeviceComment comment) {
        DeviceComment saved = deviceCommentService.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceComment> updateComment(@PathVariable Long id, @RequestBody DeviceComment comment) {
        if (!deviceCommentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        comment.setId(id);
        DeviceComment updated = deviceCommentService.save(comment);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (!deviceCommentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        deviceCommentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
