package eu.ageekatyourservice.vadinvoicing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.repository.CustomerRepository;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InterventionLogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private InterventionLogRepository logRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setup() {
        logRepository.deleteAll();
        deviceRepository.deleteAll();
        customerRepository.deleteAll();

        customer = new Customer();
        customer.setName("Customer A");
        customer.setRules(List.of(".*ERROR.*", ".*timeout.*"));
        customer = customerRepository.save(customer);

        Device d1 = new Device();
        d1.setId(123456789L);
        d1.setUsername("device123");
        d1.setCustomer(customer);
        deviceRepository.save(d1);

        InterventionLog l1 = new InterventionLog(null, LocalDateTime.now(), "c1", "device123", "Connected successfully", 1, 1);
        InterventionLog l2 = new InterventionLog(null, LocalDateTime.now(), "c1", "admin", "ERROR: Connection failed", 1, 1);
        InterventionLog l3 = new InterventionLog(null, LocalDateTime.now(), "c1", "guest", "INFO: startup complete", 1, 1);
        logRepository.saveAll(List.of(l1, l2, l3));
    }

    @Test
    void filtersLogsByCustomerRulesAndDevices() throws Exception {
        String response = mockMvc.perform(get("/api/logs")
                        .param("customerId", customer.getId().toString())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Page JSON -> ensure content has l1 and l2
        var node = objectMapper.readTree(response);
        var content = node.get("content");
        assertThat(content).isNotNull();
        var usernames = content.findValuesAsText("username");
        var messages = content.findValuesAsText("description");
        assertThat(usernames).contains("device123", "admin");
        assertThat(messages).anyMatch(m -> m.contains("Connected successfully"));
        assertThat(messages).anyMatch(m -> m.contains("ERROR: Connection failed"));
    }
}
