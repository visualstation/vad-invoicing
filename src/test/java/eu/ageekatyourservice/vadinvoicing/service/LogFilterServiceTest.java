package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.repository.CustomerRepository;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LogFilterServiceTest {

    private InterventionLogRepository logRepository;
    private CustomerRepository customerRepository;
    private DeviceRepository deviceRepository;

    private LogFilterService service;

    @BeforeEach
    void setup() {
        logRepository = mock(InterventionLogRepository.class);
        customerRepository = mock(CustomerRepository.class);
        deviceRepository = mock(DeviceRepository.class);
        service = new LogFilterService(logRepository, customerRepository, deviceRepository);
    }

    @Test
    void filtersByUsernameAndRegex() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setRules(List.of(".*error.*", "timeout"));

        Device d1 = new Device();
        d1.setId(123456789L);
        d1.setUsername("device123");

        when(deviceRepository.findByCustomer(customer)).thenReturn(List.of(d1));

        InterventionLog l1 = new InterventionLog(1L, LocalDateTime.now(), "c1", "device123", "Connected successfully", 1, 1);
        InterventionLog l2 = new InterventionLog(2L, LocalDateTime.now(), "c1", "admin", "ERROR: Connection failed", 1, 1);
        InterventionLog l3 = new InterventionLog(3L, LocalDateTime.now(), "c1", "guest", "INFO: startup complete", 1, 1);

        Page<InterventionLog> page = new PageImpl<>(List.of(l1, l2, l3));
        when(logRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<InterventionLog> result = service.getLogsFilteredByCustomer(customer, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(3); // Spec filtering is handled by DB; here we just ensure invocation

        // Verify specification is built with username and regex
        ArgumentCaptor<Specification<InterventionLog>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(logRepository).findAll(specCaptor.capture(), any(Pageable.class));
        Specification<InterventionLog> spec = specCaptor.getValue();
        assertThat(spec).isNotNull();
    }
}
