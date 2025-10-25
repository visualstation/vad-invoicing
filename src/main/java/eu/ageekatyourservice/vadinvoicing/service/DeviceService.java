package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    
    @Autowired
    private DeviceRepository repository;
    
    public List<Device> getAllDevices() {
        return repository.findAll();
    }
    
    public Optional<Device> getDeviceById(Long deviceId) {
        return repository.findById(deviceId);
    }
    
    public Device saveDevice(Device device) {
        return repository.save(device);
    }
    
    public void deleteDevice(Long deviceId) {
        repository.deleteById(deviceId);
    }
    
    public List<Device> searchByName(String name) {
        return repository.findByNameContaining(name);
    }
    
    public List<Device> getDevicesByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId);
    }
    
    public boolean existsByDeviceId(Long deviceId) {
        return repository.existsByDeviceId(deviceId);
    }
}
