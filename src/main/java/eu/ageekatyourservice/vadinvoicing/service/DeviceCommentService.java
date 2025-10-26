package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.model.Device;
import eu.ageekatyourservice.vadinvoicing.model.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceCommentService {

    private final DeviceCommentRepository repository;

    public DeviceCommentService(DeviceCommentRepository repository) {
        this.repository = repository;
    }

    public List<DeviceComment> findAllDeviceComments() {
        return repository.findAll();
    }

    public Optional<DeviceComment> findDeviceCommentById(Long id) {
        return repository.findById(id);
    }

    public List<DeviceComment> findByDevice(Device device) {
        return repository.findByDeviceOrderByCreatedAtDesc(device);
    }

    public List<DeviceComment> findByDeviceId(Long deviceId) {
        return repository.findByDeviceId(deviceId);
    }

    public DeviceComment saveDeviceComment(DeviceComment comment) {
        return repository.save(comment);
    }

    public void deleteDeviceComment(Long id) {
        repository.deleteById(id);
    }
}
