package eu.ageekatyourservice.vadinvoicing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiInfoController {
    @GetMapping
    public Map<String, Object> info() {
        return Map.of(
                "name", "VAD Invoicing API",
                "version", "v1"
        );
    }
}
