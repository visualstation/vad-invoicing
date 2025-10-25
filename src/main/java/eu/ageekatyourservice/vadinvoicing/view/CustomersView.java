package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("customers")
@PageTitle("Customers")
@PermitAll
public class CustomersView extends VerticalLayout {

    private final CustomerService customerService;
    private final AuthenticationContext authenticationContext;

    private final Grid<Customer> grid = new Grid<>(Customer.class, false);

    // Toolbar controls
    private final TextField filter = new TextField();
    private final Button addNewButton = new Button("Add customer");
    private final Button deleteButton = new Button("Delete");

    // Form fields
    private final TextField nameField = new TextField("Name");
    private final TextArea addressField = new TextArea("Address");
    private final TextField websiteField = new TextField("Website");
    private final TextField contactField = new TextField("Contact");
    private final TextField billingRateField = new TextField("Billing rate");
    private final TextField phoneNumberField = new TextField("Phone number");
    private final TextField emailField = new TextField("Email");

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    private final Binder<Customer> binder = new Binder<>(Customer.class);
    private Customer currentCustomer;

    @Autowired
    public CustomersView(CustomerService customerService, AuthenticationContext authenticationContext) {
        this.customerService = customerService;
        this.authenticationContext = authenticationContext;

        addClassName("customers-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(
            createHeader(),
            createToolbar(),
            createContent()
        );

        updateList();
        clearForm();
    }

    private HorizontalLayout createHeader() {
        H1 title = new H1("Customers");

        RouterLink logsLink = new RouterLink("Logs", MainView.class);

        Button logoutButton = new Button("Logout");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logoutButton.addClickListener(e -> authenticationContext.logout());

        HorizontalLayout right = new HorizontalLayout(logsLink, logoutButton);
        right.setAlignItems(Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(title, right);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        return header;
    }

    private HorizontalLayout createToolbar() {
        filter.setPlaceholder("Filter by name or email...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateList());
        filter.setWidth("400px");

        addNewButton.addClickListener(e -> addCustomer());
        deleteButton.addClickListener(e -> deleteSelected());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout toolbar = new HorizontalLayout(filter, addNewButton, deleteButton);
        toolbar.setAlignItems(Alignment.CENTER);
        return toolbar;
    }

    private HorizontalLayout createContent() {
        VerticalLayout form = createFormLayout();
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        grid.setWidthFull();
        return content;
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.setSizeFull();

        grid.addColumn(Customer::getId).setHeader("ID").setWidth("110px").setFlexGrow(0).setSortable(true);
        grid.addColumn(Customer::getName).setHeader("Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(Customer::getEmail).setHeader("Email").setAutoWidth(true).setSortable(true);
        grid.addColumn(Customer::getPhoneNumber).setHeader("Phone").setAutoWidth(true);
        grid.addColumn(c -> c.getBillingRate() != null ? c.getBillingRate().toPlainString() : "")
            .setHeader("Rate")
            .setWidth("120px")
            .setFlexGrow(0)
            .setSortable(true);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                editCustomer(event.getValue());
            } else {
                clearForm();
            }
        });
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();

        nameField.setRequiredIndicatorVisible(true);
        addressField.setMaxHeight("150px");
        billingRateField.setPlaceholder("e.g. 95.00");

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        formLayout.add(nameField, addressField, websiteField, contactField, billingRateField, phoneNumberField, emailField, buttons);
        formLayout.setWidth("480px");
        formLayout.setPadding(false);

        return formLayout;
    }

    private void configureForm() {
        binder.forField(nameField)
            .asRequired("Name is required")
            .bind(Customer::getName, Customer::setName);

        binder.forField(addressField)
            .bind(Customer::getAddress, Customer::setAddress);

        binder.forField(websiteField)
            .bind(Customer::getWebsite, Customer::setWebsite);

        binder.forField(contactField)
            .bind(Customer::getContact, Customer::setContact);

        binder.forField(billingRateField)
            .withConverter(new StringToBigDecimalConverter("Must be a number"))
            .bind(Customer::getBillingRate, Customer::setBillingRate);

        binder.forField(phoneNumberField)
            .bind(Customer::getPhoneNumber, Customer::setPhoneNumber);

        binder.forField(emailField)
            .bind(Customer::getEmail, Customer::setEmail);

        saveButton.addClickListener(e -> saveCustomer());
        cancelButton.addClickListener(e -> cancelEdit());
    }

    private void addCustomer() {
        grid.asSingleSelect().clear();
        editCustomer(new Customer());
    }

    private void editCustomer(Customer customer) {
        this.currentCustomer = customer;
        binder.readBean(customer);
    }

    private void clearForm() {
        this.currentCustomer = null;
        nameField.clear();
        addressField.clear();
        websiteField.clear();
        contactField.clear();
        billingRateField.clear();
        phoneNumberField.clear();
        emailField.clear();
    }

    private void cancelEdit() {
        if (currentCustomer != null && currentCustomer.getId() != null) {
            customerService.getCustomerById(currentCustomer.getId()).ifPresent(this::editCustomer);
        } else {
            clearForm();
        }
    }

    private void saveCustomer() {
        if (currentCustomer == null) {
            currentCustomer = new Customer();
        }

        try {
            binder.writeBean(currentCustomer);
            customerService.saveCustomer(currentCustomer);
            Notification.show("Customer saved", 2000, Notification.Position.MIDDLE);
            updateList();
            grid.asSingleSelect().clear();
            editCustomer(currentCustomer);
        } catch (ValidationException ex) {
            Notification.show("Fix validation errors before saving", 3000, Notification.Position.MIDDLE);
        }
    }

    private void deleteSelected() {
        Customer selected = grid.asSingleSelect().getValue();
        if (selected == null || selected.getId() == null) {
            Notification.show("Select a customer to delete", 2000, Notification.Position.MIDDLE);
            return;
        }
        customerService.deleteCustomer(selected.getId());
        Notification.show("Customer deleted", 2000, Notification.Position.MIDDLE);
        updateList();
        clearForm();
    }

    private void updateList() {
        String value = filter.getValue();
        List<Customer> items;
        if (value == null || value.isBlank()) {
            items = customerService.getAllCustomers();
        } else {
            String v = value.toLowerCase();
            items = customerService.getAllCustomers().stream()
                .filter(c ->
                    (c.getName() != null && c.getName().toLowerCase().contains(v)) ||
                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(v))
                )
                .toList();
        }
        grid.setItems(items);
    }
}
