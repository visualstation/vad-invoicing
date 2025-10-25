package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceCommentService {

    @Autowired
    private DeviceCommentRepository repository;

    public List<DeviceComment> findByDevice(Device device) {
        return repository.findByDeviceOrderByCreatedAtDesc(device);
    }

    public DeviceComment save(DeviceComment comment) {
        return repository.save(comment);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
