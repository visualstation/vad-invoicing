package eu.ageekatyourservice.vadinvoicing.service;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.exception.ResourceNotFoundException;
import eu.ageekatyourservice.vadinvoicing.repository.CustomerRepository;
import eu.ageekatyourservice.vadinvoicing.repository.DeviceRepository;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class LogFilterService {

    private final InterventionLogRepository logRepository;
    private final CustomerRepository customerRepository;
    private final DeviceRepository deviceRepository;

    public LogFilterService(
            InterventionLogRepository logRepository,
            CustomerRepository customerRepository,
            DeviceRepository deviceRepository
    ) {
        this.logRepository = logRepository;
        this.customerRepository = customerRepository;
        this.deviceRepository = deviceRepository;
    }

    public Page<InterventionLog> getLogsFilteredByCustomerId(Long customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " not found"));
        return getLogsFilteredByCustomer(customer, pageable);
    }

    public Page<InterventionLog> getLogsFilteredByCustomer(Customer customer, Pageable pageable) {
        // Build usernames from devices via repository to avoid lazy init issues
        Set<String> deviceUsernames = new HashSet<>();
        for (Device d : deviceRepository.findByCustomer(customer)) {
            if (d.getUsername() != null && !d.getUsername().isBlank()) {
                deviceUsernames.add(d.getUsername());
            }
        }

        // Precompile patterns (also used to derive LIKE expressions)
        List<Pattern> compiledRules = new ArrayList<>();
        List<String> likeFragments = new ArrayList<>();
        if (customer.getRules() != null) {
            for (String rule : customer.getRules()) {
                if (rule == null || rule.isBlank()) continue;
                try {
                    compiledRules.add(Pattern.compile(rule));
                } catch (Exception ignored) {
                    // ignore invalid regex
                }
                String like = regexToSqlLike(rule);
                if (like != null) {
                    likeFragments.add(like);
                }
            }
        }

        Specification<InterventionLog> spec = buildSpecification(deviceUsernames, likeFragments);
        return logRepository.findAll(spec, pageable);
    }

    private Specification<InterventionLog> buildSpecification(Set<String> usernames, List<String> likePatterns) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> disjunction = new ArrayList<>();
            if (!usernames.isEmpty()) {
                disjunction.add(root.get("username").in(usernames));
            }
            if (!likePatterns.isEmpty()) {
                List<jakarta.persistence.criteria.Predicate> likePreds = new ArrayList<>();
                for (String like : likePatterns) {
                    likePreds.add(cb.like(cb.lower(root.get("description")), like.toLowerCase(Locale.ROOT)));
                }
                disjunction.add(cb.or(likePreds.toArray(new jakarta.persistence.criteria.Predicate[0])));
            }
            if (disjunction.isEmpty()) {
                return cb.conjunction();
            }
            return cb.or(disjunction.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    // Best-effort conversion of common regex (like ".*foo.*") to SQL LIKE pattern
    private String regexToSqlLike(String regex) {
        if (regex == null) return null;
        String r = regex.trim();
        // Strip anchors
        if (r.startsWith("^")) r = r.substring(1);
        if (r.endsWith("$")) r = r.substring(0, r.length() - 1);
        // Replace common wildcard
        r = r.replace(".*", "%");
        // Escape SQL LIKE special chars left in literal portions
        r = r.replace("%", "%"); // already converted; leave as is
        r = r.replace("_", "\\_");
        // If after conversion there is no % around, wrap with % to make it contains
        boolean hasWildcard = r.contains("%");
        if (!hasWildcard) {
            r = "%" + r + "%";
        }
        // If the regex still looks complex (character classes, groups), return a broad contains
        if (regex.matches(".*[\\[\\]\\(\\)\\+\\?\\|].*")) {
            return "%" + regex.replaceAll("[\\^$.*+?()[\\]{}|\\\\]", "").toLowerCase(Locale.ROOT) + "%";
        }
        return r;
    }
}
