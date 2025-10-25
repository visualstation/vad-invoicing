package eu.ageekatyourservice.vadinvoicing.repository;

import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceCommentRepository extends JpaRepository<DeviceComment, Long> {
    
    List<DeviceComment> findByDeviceDeviceIdOrderByCommentDateDesc(Long deviceId);
}
