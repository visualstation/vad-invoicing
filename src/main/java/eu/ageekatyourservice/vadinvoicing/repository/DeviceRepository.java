package eu.ageekatyourservice.vadinvoicing.repository;

import eu.ageekatyourservice.vadinvoicing.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    List<Device> findByNameContaining(String name);
    
    List<Device> findByCustomerId(Long customerId);
    
    boolean existsByDeviceId(Long deviceId);
}
