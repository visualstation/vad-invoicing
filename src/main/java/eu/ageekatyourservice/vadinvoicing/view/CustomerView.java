package eu.ageekatyourservice.vadinvoicing.view;

import eu.ageekatyourservice.vadinvoicing.model.Customer;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "customers", layout = MainLayout.class)
@PageTitle("Customers | VAD Invoicing")
@PermitAll
public class CustomerView extends VerticalLayout {
    
    private final CustomerService customerService;
    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    private final TextField filterText = new TextField();
    
    private Customer selectedCustomer;
    
    @Autowired
    public CustomerView(CustomerService customerService, AuthenticationContext authenticationContext) {
        this.customerService = customerService;
        
        addClassName("customer-view");
        setSizeFull();
        setPadding(true);
        
        configureGrid();
        
        add(
            createToolbar(),
            grid
        );
        
        updateList();
    }
    
    private HorizontalLayout createToolbar() {
        filterText.setPlaceholder("Filter by name or email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("400px");
        
        Button addButton = new Button("Add Customer", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openCustomerDialog(null));
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> updateList());
        
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, refreshButton);
        toolbar.setAlignItems(Alignment.CENTER);
        
        return toolbar;
    }
    
    private void configureGrid() {
        grid.addClassName("customer-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        
        grid.addColumn(Customer::getName)
            .setHeader("Name")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(Customer::getEmail)
            .setHeader("Email")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(Customer::getPhoneNumber)
            .setHeader("Phone")
            .setSortable(true)
            .setWidth("150px")
            .setFlexGrow(0);
        
        grid.addColumn(Customer::getContact)
            .setHeader("Contact Person")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(Customer::getBillingRate)
            .setHeader("Billing Rate")
            .setSortable(true)
            .setWidth("130px")
            .setFlexGrow(0);
        
        grid.addComponentColumn(customer -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openCustomerDialog(customer));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(customer));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions").setWidth("130px").setFlexGrow(0);
    }
    
    private void openCustomerDialog(Customer customer) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        
        boolean isNew = customer == null;
        selectedCustomer = isNew ? new Customer() : customer;
        
        H2 title = new H2(isNew ? "Add New Customer" : "Edit Customer");
        
        // Create form fields
        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        nameField.setWidthFull();
        
        EmailField emailField = new EmailField("Email");
        emailField.setWidthFull();
        
        TextField phoneField = new TextField("Phone Number");
        phoneField.setWidthFull();
        
        TextField contactField = new TextField("Contact Person");
        contactField.setWidthFull();
        
        TextField websiteField = new TextField("Website");
        websiteField.setWidthFull();
        
        TextArea addressArea = new TextArea("Address");
        addressArea.setWidthFull();
        addressArea.setMaxHeight("100px");
        
        BigDecimalField billingRateField = new BigDecimalField("Billing Rate");
        billingRateField.setWidthFull();
        
        // Create binder
        Binder<Customer> binder = new BeanValidationBinder<>(Customer.class);
        binder.forField(nameField)
            .asRequired("Name is required")
            .bind(Customer::getName, Customer::setName);
        binder.bind(emailField, Customer::getEmail, Customer::setEmail);
        binder.bind(phoneField, Customer::getPhoneNumber, Customer::setPhoneNumber);
        binder.bind(contactField, Customer::getContact, Customer::setContact);
        binder.bind(websiteField, Customer::getWebsite, Customer::setWebsite);
        binder.bind(addressArea, Customer::getAddress, Customer::setAddress);
        binder.bind(billingRateField, Customer::getBillingRate, Customer::setBillingRate);
        
        binder.readBean(selectedCustomer);
        
        // Create form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, emailField, phoneField, contactField, websiteField, billingRateField, addressArea);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(addressArea, 2);
        
        // Create buttons
        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(selectedCustomer);
                customerService.saveCustomer(selectedCustomer);
                updateList();
                dialog.close();
                showNotification(isNew ? "Customer created successfully" : "Customer updated successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error saving customer: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        
        Button cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
        cancelButton.addClickListener(e -> dialog.close());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();
        
        VerticalLayout dialogLayout = new VerticalLayout(title, formLayout, buttonLayout);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private void confirmDelete(Customer customer) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");
        
        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Are you sure you want to delete customer: " + customer.getName() + "?");
        textLayout.setPadding(false);
        
        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                customerService.deleteCustomer(customer.getId());
                updateList();
                confirmDialog.close();
                showNotification("Customer deleted successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error deleting customer: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> confirmDialog.close());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, cancelButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();
        
        VerticalLayout dialogLayout = new VerticalLayout(title, textLayout, buttonLayout);
        dialogLayout.setPadding(true);
        
        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }
    
    private void updateList() {
        String filterValue = filterText.getValue();
        
        if (filterValue == null || filterValue.isEmpty()) {
            grid.setItems(customerService.findAllCustomers());
        } else {
            grid.setItems(customerService.findAllCustomers().stream()
                .filter(customer -> 
                    (customer.getName() != null && customer.getName().toLowerCase().contains(filterValue.toLowerCase())) ||
                    (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(filterValue.toLowerCase()))
                )
                .toList()
            );
        }
    }
    
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
