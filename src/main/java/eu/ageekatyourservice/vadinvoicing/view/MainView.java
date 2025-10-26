package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import eu.ageekatyourservice.vadinvoicing.service.DeviceService;
import eu.ageekatyourservice.vadinvoicing.service.InterventionLogService;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | VAD Invoicing")
@PermitAll
public class MainView extends VerticalLayout {
    
    private final CustomerService customerService;
    private final DeviceService deviceService;
    private final InterventionLogService logService;
    
    public MainView(CustomerService customerService, 
                    DeviceService deviceService,
                    InterventionLogService logService,
                    AuthenticationContext authenticationContext) {
        this.customerService = customerService;
        this.deviceService = deviceService;
        this.logService = logService;
        
        addClassName("dashboard-view");
        setSizeFull();
        setPadding(true);
        
        H1 title = new H1("Dashboard");
        
        Paragraph welcomeText = new Paragraph(
            "Welcome to VAD Invoicing System. Manage your customers, devices, and intervention logs efficiently."
        );
        
        // Statistics cards
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        
        statsLayout.add(
            createStatCard("Total Customers", 
                String.valueOf(customerService.findAllCustomers().size()), 
                VaadinIcon.USERS, 
                "#2196F3"),
            createStatCard("Total Devices", 
                String.valueOf(deviceService.findAllDevices().size()), 
                VaadinIcon.LAPTOP, 
                "#4CAF50"),
            createStatCard("Total Logs", 
                String.valueOf(logService.findAllInterventionLogs().size()), 
                VaadinIcon.FILE_TEXT, 
                "#FF9800")
        );
        
        // Quick actions section
        H2 quickActionsTitle = new H2("Quick Actions");
        Paragraph quickActionsText = new Paragraph(
            "Use the sidebar on the left to navigate to different sections of the application."
        );
        
        add(title, welcomeText, statsLayout, quickActionsTitle, quickActionsText);
    }
    
    private Div createStatCard(String title, String value, VaadinIcon iconType, String color) {
        Div card = new Div();
        card.getStyle()
            .set("padding", "20px")
            .set("background-color", "var(--lumo-contrast-5pct)")
            .set("border-radius", "8px")
            .set("border-left", "4px solid " + color)
            .set("flex", "1");
        
        Icon icon = iconType.create();
        icon.setSize("48px");
        icon.getStyle().set("color", color);
        
        H2 valueText = new H2(value);
        valueText.getStyle()
            .set("margin", "10px 0")
            .set("color", color);
        
        Span titleText = new Span(title);
        titleText.getStyle()
            .set("color", "var(--lumo-secondary-text-color)")
            .set("font-size", "var(--lumo-font-size-s)");
        
        VerticalLayout content = new VerticalLayout(icon, valueText, titleText);
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(Alignment.START);
        
        card.add(content);
        
        return card;
    }
}
