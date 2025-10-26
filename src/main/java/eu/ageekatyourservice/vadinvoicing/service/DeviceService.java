package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.model.Customer;
import eu.ageekatyourservice.vadinvoicing.model.Device;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    public List<Device> findAllDevices() {
        return repository.findAll();
    }

    public Optional<Device> findDeviceById(Long id) {
        return repository.findById(id);
    }

    public Device saveDevice(Device device) {
        return repository.save(device);
    }

    public void deleteDevice(Long id) {
        repository.deleteById(id);
    }

    public List<Device> findByCustomer(Customer customer) {
        return repository.findByCustomer(customer);
    }

    public List<Device> findByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    public List<Device> searchByLabel(String label) {
        return repository.findByLabelContainingIgnoreCase(label);
    }
}
