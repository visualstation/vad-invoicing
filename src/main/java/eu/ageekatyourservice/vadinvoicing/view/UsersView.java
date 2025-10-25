package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import eu.ageekatyourservice.vadinvoicing.entity.User;
import eu.ageekatyourservice.vadinvoicing.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users | VAD Invoicing")
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService userService;

    private final Grid<User> grid = new Grid<>(User.class, false);
    private final TextField filterText = new TextField();

    private User selectedUser;

    @Autowired
    public UsersView(UserService userService, AuthenticationContext authenticationContext) {
        this.userService = userService;

        addClassName("users-view");
        setSizeFull();
        setPadding(true);

        H1 title = new H1("Users");
        add(title);

        configureGrid();

        add(
            createToolbar(),
            grid
        );

        updateList();
    }

    private HorizontalLayout createToolbar() {
        filterText.setPlaceholder("Filter by username...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("400px");

        Button addButton = new Button("Add User", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openUserDialog(null));

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, refreshButton);
        toolbar.setAlignItems(Alignment.CENTER);

        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("users-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        grid.addColumn(User::getId)
            .setHeader("ID")
            .setSortable(true)
            .setWidth("100px")
            .setFlexGrow(0);

        grid.addColumn(User::getUsername)
            .setHeader("Username")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);

        grid.addColumn(User::getRole)
            .setHeader("Role")
            .setSortable(true)
            .setWidth("200px")
            .setFlexGrow(0);

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
        dialog.setWidth("500px");

        boolean isNew = user == null;
        User formBean = isNew ? new User() : new User(user.getId(), user.getUsername(), null, user.getRole());

        H2 title = new H2(isNew ? "Add New User" : "Edit User");

        TextField usernameField = new TextField("Username");
        usernameField.setRequired(true);
        usernameField.setWidthFull();

        PasswordField passwordField = new PasswordField(isNew ? "Password" : "New Password (optional)");
        passwordField.setWidthFull();
        if (!isNew) {
            passwordField.setHelperText("Leave empty to keep current password");
        }

        ComboBox<String> roleCombo = new ComboBox<>("Role");
        roleCombo.setItems("ROLE_USER", "ROLE_ADMIN");
        roleCombo.setWidthFull();

        Binder<User> binder = new BeanValidationBinder<>(User.class);
        binder.forField(usernameField)
            .asRequired("Username is required")
            .bind(User::getUsername, User::setUsername);
        binder.forField(roleCombo)
            .asRequired("Role is required")
            .bind(User::getRole, User::setRole);

        binder.readBean(formBean);

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, roleCombo, passwordField);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(usernameField, 2);
        formLayout.setColspan(passwordField, 2);

        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(formBean);
                userService.save(formBean, passwordField.getValue());
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
        textLayout.add("Delete user: " + user.getUsername() + "?");
        textLayout.setPadding(false);

        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                userService.delete(user.getId());
                updateList();
                confirmDialog.close();
                showNotification("User deleted", NotificationVariant.LUMO_SUCCESS);
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
            grid.setItems(userService.findAll());
        } else {
            grid.setItems(userService.findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().toLowerCase().contains(filterValue.toLowerCase()))
                .toList());
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
