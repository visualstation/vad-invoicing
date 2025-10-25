package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {
    
    private final AuthenticationContext authenticationContext;
    
    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        
        createHeader();
        createDrawer();
    }
    
    private void createHeader() {
        H1 appTitle = new H1("VAD Invoicing");
        appTitle.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.NONE
        );
        
        Button logoutButton = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClickListener(e -> authenticationContext.logout());
        
        DrawerToggle toggle = new DrawerToggle();
        
        HorizontalLayout header = new HorizontalLayout(toggle, appTitle, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(appTitle);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        );
        
        addToNavbar(header);
    }
    
    private void createDrawer() {
        VerticalLayout drawerLayout = new VerticalLayout();
        drawerLayout.setSizeFull();
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(false);
        drawerLayout.getStyle()
            .set("background-color", "white");
        
        // App info section
        VerticalLayout appInfo = new VerticalLayout();
        appInfo.setPadding(true);
        appInfo.setSpacing(false);
        
        H1 logo = new H1("VAD");
        logo.addClassNames(
            LumoUtility.FontSize.XLARGE,
            LumoUtility.Margin.NONE
        );
        logo.getStyle()
            .set("color", "var(--lumo-primary-color)")
            .set("font-weight", "bold");
        
        Span subtitle = new Span("Invoicing System");
        subtitle.addClassNames(
            LumoUtility.FontSize.SMALL,
            LumoUtility.TextColor.SECONDARY
        );
        
        appInfo.add(logo, subtitle);
        
        // Navigation menu
        VerticalLayout navigation = new VerticalLayout();
        navigation.setPadding(false);
        navigation.setSpacing(false);
        
        navigation.add(
            createMenuHeader("Main"),
            createNavLink("Dashboard", VaadinIcon.DASHBOARD, ""),
            new Hr(),
            createMenuHeader("Management"),
            createNavLink("Customers", VaadinIcon.USERS, "customers"),
            createNavLink("Devices", VaadinIcon.LAPTOP, "devices"),
            new Hr(),
            createMenuHeader("System"),
            createNavLink("Intervention Logs", VaadinIcon.FILE_TEXT, "logs"),
            createNavLink("Users", VaadinIcon.USER, "users")
        );
        
        drawerLayout.add(appInfo, navigation);
        
        addToDrawer(drawerLayout);
    }
    
    private Span createMenuHeader(String title) {
        Span header = new Span(title);
        header.addClassNames(
            LumoUtility.FontSize.XSMALL,
            LumoUtility.FontWeight.BOLD,
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.Padding.Vertical.SMALL
        );
        header.getStyle().set("text-transform", "uppercase");
        return header;
    }
    
    private RouterLink createNavLink(String text, VaadinIcon icon, String route) {
        Icon iconComponent = icon.create();
        iconComponent.getStyle()
            .set("margin-inline-end", "var(--lumo-space-s)")
            .set("color", "var(--lumo-contrast-60pct)");
        
        Span label = new Span(text);
        
        HorizontalLayout layout = new HorizontalLayout(iconComponent, label);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM);
        
        Class<? extends com.vaadin.flow.component.Component> routeClass;
        if (route.isEmpty()) {
            routeClass = MainView.class;
        } else {
            routeClass = getRouteClass(route);
        }
        
        RouterLink link = new RouterLink();
        link.add(layout);
        link.setRoute(routeClass);
        link.getStyle()
            .set("text-decoration", "none")
            .set("color", "var(--lumo-body-text-color)")
            .set("display", "block");
        
        link.addClassName("nav-link");
        
        // Hover effect
        link.getElement().addEventListener("mouseenter", e -> {
            layout.getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
        });
        link.getElement().addEventListener("mouseleave", e -> {
            layout.getStyle().set("background-color", "transparent");
        });
        
        return link;
    }
    
    private Class<? extends com.vaadin.flow.component.Component> getRouteClass(String route) {
        return switch (route) {
            case "customers" -> CustomerView.class;
            case "devices" -> DeviceView.class;
            case "logs" -> InterventionLogsView.class;
            case "users" -> UsersView.class;
            default -> MainView.class;
        };
    }
}
