package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceCommentService {
    
    @Autowired
    private DeviceCommentRepository repository;
    
    public List<DeviceComment> getAllComments() {
        return repository.findAll();
    }
    
    public Optional<DeviceComment> getCommentById(Long id) {
        return repository.findById(id);
    }
    
    public DeviceComment saveComment(DeviceComment comment) {
        return repository.save(comment);
    }
    
    public void deleteComment(Long id) {
        repository.deleteById(id);
    }
    
    public List<DeviceComment> getCommentsByDeviceId(Long deviceId) {
        return repository.findByDeviceDeviceIdOrderByCommentDateDesc(deviceId);
    }
}
