package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    public List<Device> getAll() {
        return repository.findAll();
    }

    public Optional<Device> getById(Long id) {
        return repository.findById(id);
    }

    public Device save(Device device) {
        return repository.save(device);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Device> findByCustomer(Customer customer) {
        return repository.findByCustomer(customer);
    }

    public List<Device> searchByLabel(String label) {
        return repository.findByLabelContainingIgnoreCase(label);
    }
}
