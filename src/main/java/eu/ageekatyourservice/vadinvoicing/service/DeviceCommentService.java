package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DeviceCommentService {

    private final DeviceCommentRepository repository;

    public DeviceCommentService(DeviceCommentRepository repository) {
        this.repository = repository;
    }

    public List<DeviceComment> findAll() {
        return repository.findAll();
    }

    public List<DeviceComment> findByDevice(Device device) {
        return repository.findByDeviceOrderByCreatedAtDesc(device);
    }

    public DeviceComment save(DeviceComment comment) {
        return repository.save(comment);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public DeviceComment findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("DeviceComment " + id + " not found"));
    }
}
