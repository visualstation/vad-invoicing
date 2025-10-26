package eu.ageekatyourservice.vadinvoicing.view;

import eu.ageekatyourservice.vadinvoicing.model.User;
import eu.ageekatyourservice.vadinvoicing.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users | VAD Invoicing")
@PermitAll
public class UserView extends VerticalLayout {
    
    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final TextField filterText = new TextField();
    
    private User selectedUser;
    
    @Autowired
    public UserView(UserService userService, AuthenticationContext authenticationContext) {
        this.userService = userService;
        
        addClassName("user-view");
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
        filterText.setPlaceholder("Filter by username or role...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("400px");
        
        Button addButton = new Button("Add User", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openUserDialog(null));
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> updateList());
        
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, refreshButton);
        toolbar.setAlignItems(Alignment.CENTER);
        
        return toolbar;
    }
    
    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        
        grid.addColumn(User::getId)
            .setHeader("ID")
            .setSortable(true)
            .setWidth("80px")
            .setFlexGrow(0);
        
        grid.addColumn(User::getUsername)
            .setHeader("Username")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(User::getRole)
            .setHeader("Role")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addComponentColumn(user -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openUserDialog(user));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(user));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions").setWidth("130px").setFlexGrow(0);
    }
    
    private void openUserDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        
        boolean isNew = user == null;
        selectedUser = isNew ? new User() : user;
        
        H2 title = new H2(isNew ? "Add New User" : "Edit User");
        
        // Create form fields
        TextField usernameField = new TextField("Username");
        usernameField.setRequired(true);
        usernameField.setWidthFull();
        
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidthFull();
        if (isNew) {
            passwordField.setRequired(true);
            passwordField.setHelperText("Password is required for new users");
        } else {
            passwordField.setHelperText("Leave blank to keep current password");
        }
        
        Select<String> roleSelect = new Select<>();
        roleSelect.setLabel("Role");
        roleSelect.setItems("ROLE_USER", "ROLE_ADMIN");
        roleSelect.setWidthFull();
        
        // Create binder
        Binder<User> binder = new Binder<>(User.class);
        binder.forField(usernameField)
            .asRequired("Username is required")
            .bind(User::getUsername, User::setUsername);
        binder.bind(roleSelect, User::getRole, User::setRole);
        
        binder.readBean(selectedUser);
        
        // Create form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, passwordField, roleSelect);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1)
        );
        
        // Create buttons
        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(selectedUser);
                
                String plainPassword = passwordField.getValue();
                if (isNew) {
                    if (plainPassword == null || plainPassword.isEmpty()) {
                        showNotification("Password is required for new users", NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    userService.createUser(selectedUser);
                } else {
                    userService.updateUser(selectedUser);
                }
                
                updateList();
                dialog.close();
                showNotification(isNew ? "User created successfully" : "User updated successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error saving user: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
    
    private void confirmDelete(User user) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");
        
        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Are you sure you want to delete user: " + user.getUsername() + "?");
        textLayout.setPadding(false);
        
        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                userService.deleteUser(user.getId());
                updateList();
                confirmDialog.close();
                showNotification("User deleted successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error deleting user: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
            grid.setItems(userService.findAllUsers());
        } else {
            grid.setItems(userService.findAllUsers().stream()
                .filter(user -> 
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(filterValue.toLowerCase())) ||
                    (user.getRole() != null && user.getRole().toLowerCase().contains(filterValue.toLowerCase()))
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
