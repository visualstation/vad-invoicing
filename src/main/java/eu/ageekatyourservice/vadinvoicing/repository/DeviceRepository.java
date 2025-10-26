package eu.ageekatyourservice.vadinvoicing.repository;

import eu.ageekatyourservice.vadinvoicing.model.Customer;
import eu.ageekatyourservice.vadinvoicing.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByCustomer(Customer customer);
    List<Device> findByCustomerId(Long customerId);
    List<Device> findByLabelContainingIgnoreCase(String label);
}
