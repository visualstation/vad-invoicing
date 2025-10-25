package eu.ageekatyourservice.vadinvoicing;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "vad-invoicing")
public class VadInvoicingApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(VadInvoicingApplication.class, args);
    }

}
