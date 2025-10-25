package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Main application layout with a left sidebar navigation and a top header.
 */
@PermitAll
public class MainLayout extends AppLayout {

    private final AuthenticationContext authenticationContext;
    private H1 viewTitle;

    @Autowired
    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER);

        addToNavbar(createTopBar());
        addToDrawer(createSideNav());
    }

    private Component createTopBar() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        H1 appName = new H1("VAD Invoicing");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        com.vaadin.flow.component.button.Button logout = new com.vaadin.flow.component.button.Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
        logout.addClickListener(e -> authenticationContext.logout());

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.NONE);

        HorizontalLayout left = new HorizontalLayout(toggle, appName);
        left.setAlignItems(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        HorizontalLayout right = new HorizontalLayout(viewTitle, logout);
        right.setWidthFull();
        right.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        right.setAlignItems(FlexComponent.Alignment.CENTER);

        Header header = new Header();
        header.getStyle().set("width", "100%");

        HorizontalLayout container = new HorizontalLayout(left, right);
        container.setWidthFull();
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        header.add(container);
        return header;
    }

    private Component createSideNav() {
        SideNav nav = new SideNav();
        nav.setLabel("Navigation");

        nav.addItem(new SideNavItem("Intervention Logs", MainView.class, VaadinIcon.LIST.create()));
        nav.addItem(new SideNavItem("Customers", CustomerView.class, VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Devices", DeviceView.class, VaadinIcon.DESKTOP.create()));

        // Spacer to push footer if needed
        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");
        nav.add(spacer);

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        // Update the view title in the header based on @PageTitle of the current view
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        viewTitle.setText(title != null ? title.value() : "");
    }
}
