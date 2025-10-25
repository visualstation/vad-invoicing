package eu.ageekatyourservice.vadinvoicing.view;

import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import eu.ageekatyourservice.vadinvoicing.service.DeviceCommentService;
import eu.ageekatyourservice.vadinvoicing.service.DeviceService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Route("devices")
@PageTitle("Devices")
@PermitAll
public class DeviceView extends VerticalLayout {
    
    private final DeviceService deviceService;
    private final DeviceCommentService commentService;
    private final CustomerService customerService;
    private final AuthenticationContext authenticationContext;
    private final Grid<Device> grid = new Grid<>(Device.class, false);
    private final TextField filterText = new TextField();
    
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Autowired
    public DeviceView(DeviceService deviceService, DeviceCommentService commentService,
                     CustomerService customerService, AuthenticationContext authenticationContext) {
        this.deviceService = deviceService;
        this.commentService = commentService;
        this.customerService = customerService;
        this.authenticationContext = authenticationContext;
        
        addClassName("device-view");
        setSizeFull();
        
        configureGrid();
        
        add(
            createHeader(),
            createToolbar(),
            grid
        );
        
        updateList();
    }
    
    private HorizontalLayout createHeader() {
        H1 title = new H1("Devices");
        
        Button backButton = new Button("Back to Main", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        
        Button logoutButton = new Button("Logout");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logoutButton.addClickListener(e -> {
            authenticationContext.logout();
        });
        
        HorizontalLayout header = new HorizontalLayout(title, backButton, logoutButton);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        
        return header;
    }
    
    private HorizontalLayout createToolbar() {
        filterText.setPlaceholder("Filter by name or model...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("400px");
        
        Button addButton = new Button("Add Device", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openDeviceDialog(null));
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> updateList());
        
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, refreshButton);
        toolbar.setAlignItems(Alignment.CENTER);
        
        return toolbar;
    }
    
    private void configureGrid() {
        grid.addClassName("device-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        
        grid.addColumn(Device::getDeviceId)
            .setHeader("Device ID")
            .setSortable(true)
            .setWidth("130px")
            .setFlexGrow(0);
        
        grid.addColumn(Device::getName)
            .setHeader("Name")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(Device::getModel)
            .setHeader("Model")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(Device::getManufacturer)
            .setHeader("Manufacturer")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(device -> device.getCustomer() != null ? device.getCustomer().getName() : "")
            .setHeader("Customer")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addComponentColumn(device -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openDeviceDialog(device));
            
            Button commentsButton = new Button(new Icon(VaadinIcon.COMMENTS));
            commentsButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            commentsButton.addClickListener(e -> openCommentsDialog(device));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(device));
            
            return new HorizontalLayout(editButton, commentsButton, deleteButton);
        }).setHeader("Actions").setWidth("180px").setFlexGrow(0);
    }
    
    private void openDeviceDialog(Device device) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        
        boolean isNew = device == null;
        Device selectedDevice = isNew ? new Device() : device;
        
        H2 title = new H2(isNew ? "Add New Device" : "Edit Device");
        
        // Create form fields
        IntegerField deviceIdField = new IntegerField("Device ID (9-10 digits)");
        deviceIdField.setRequired(true);
        deviceIdField.setWidthFull();
        deviceIdField.setMin(100000000);
        deviceIdField.setMax(999999999);
        deviceIdField.setHelperText("Enter a unique 9-10 digit number");
        if (!isNew) {
            deviceIdField.setEnabled(false);
        }
        
        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        nameField.setWidthFull();
        
        TextField modelField = new TextField("Model");
        modelField.setWidthFull();
        
        TextField manufacturerField = new TextField("Manufacturer");
        manufacturerField.setWidthFull();
        
        TextArea descriptionArea = new TextArea("Description");
        descriptionArea.setWidthFull();
        descriptionArea.setMaxHeight("100px");
        
        ComboBox<Customer> customerComboBox = new ComboBox<>("Customer");
        customerComboBox.setItems(customerService.getAllCustomers());
        customerComboBox.setItemLabelGenerator(Customer::getName);
        customerComboBox.setWidthFull();
        
        // Create binder
        Binder<Device> binder = new BeanValidationBinder<>(Device.class);
        binder.forField(deviceIdField)
            .asRequired("Device ID is required")
            .withConverter(Integer::longValue, Long::intValue)
            .withValidator(id -> id >= 100000000L && id <= 9999999999L, 
                "Device ID must be 9-10 digits")
            .withValidator(id -> isNew || id.equals(selectedDevice.getDeviceId()), 
                "Cannot change device ID")
            .withValidator(id -> !isNew || !deviceService.existsByDeviceId(id),
                "Device ID already exists")
            .bind(Device::getDeviceId, Device::setDeviceId);
        binder.forField(nameField)
            .asRequired("Name is required")
            .bind(Device::getName, Device::setName);
        binder.bind(modelField, Device::getModel, Device::setModel);
        binder.bind(manufacturerField, Device::getManufacturer, Device::setManufacturer);
        binder.bind(descriptionArea, Device::getDescription, Device::setDescription);
        binder.bind(customerComboBox, Device::getCustomer, Device::setCustomer);
        
        binder.readBean(selectedDevice);
        
        // Create form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(deviceIdField, nameField, modelField, manufacturerField, customerComboBox, descriptionArea);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(descriptionArea, 2);
        
        // Create buttons
        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(selectedDevice);
                deviceService.saveDevice(selectedDevice);
                updateList();
                dialog.close();
                showNotification(isNew ? "Device created successfully" : "Device updated successfully", 
                    NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error saving device: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
    
    private void openCommentsDialog(Device device) {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");
        dialog.setHeight("700px");
        
        H2 title = new H2("Comments for Device: " + device.getName());
        
        // Create comments grid
        Grid<DeviceComment> commentsGrid = new Grid<>(DeviceComment.class, false);
        commentsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        commentsGrid.setHeight("400px");
        
        commentsGrid.addColumn(comment -> comment.getCommentDate().format(DATE_FORMATTER))
            .setHeader("Date & Time")
            .setSortable(true)
            .setWidth("150px")
            .setFlexGrow(0);
        
        commentsGrid.addColumn(DeviceComment::getAuthor)
            .setHeader("Author")
            .setSortable(true)
            .setWidth("150px")
            .setFlexGrow(0);
        
        commentsGrid.addColumn(DeviceComment::getComment)
            .setHeader("Comment")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        commentsGrid.addComponentColumn(comment -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openCommentDialog(device, comment, commentsGrid));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDeleteComment(comment, commentsGrid));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions").setWidth("130px").setFlexGrow(0);
        
        // Load comments
        commentsGrid.setItems(commentService.getCommentsByDeviceId(device.getDeviceId()));
        
        // Add comment button
        Button addCommentButton = new Button("Add Comment", new Icon(VaadinIcon.PLUS));
        addCommentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCommentButton.addClickListener(e -> openCommentDialog(device, null, commentsGrid));
        
        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(addCommentButton, closeButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();
        
        VerticalLayout dialogLayout = new VerticalLayout(title, commentsGrid, buttonLayout);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setSizeFull();
        
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private void openCommentDialog(Device device, DeviceComment comment, Grid<DeviceComment> commentsGrid) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        
        boolean isNew = comment == null;
        DeviceComment selectedComment = isNew ? new DeviceComment() : comment;
        
        H3 title = new H3(isNew ? "Add New Comment" : "Edit Comment");
        
        DateTimePicker dateTimeField = new DateTimePicker("Date & Time");
        dateTimeField.setRequiredIndicatorVisible(true);
        dateTimeField.setWidthFull();
        if (isNew) {
            dateTimeField.setValue(LocalDateTime.now());
        }
        
        TextField authorField = new TextField("Author");
        authorField.setWidthFull();
        
        TextArea commentArea = new TextArea("Comment");
        commentArea.setRequired(true);
        commentArea.setWidthFull();
        commentArea.setHeight("150px");
        
        // Create binder
        Binder<DeviceComment> binder = new BeanValidationBinder<>(DeviceComment.class);
        binder.forField(dateTimeField)
            .asRequired("Date is required")
            .bind(DeviceComment::getCommentDate, DeviceComment::setCommentDate);
        binder.bind(authorField, DeviceComment::getAuthor, DeviceComment::setAuthor);
        binder.forField(commentArea)
            .asRequired("Comment is required")
            .bind(DeviceComment::getComment, DeviceComment::setComment);
        
        binder.readBean(selectedComment);
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(dateTimeField, authorField, commentArea);
        formLayout.setColspan(commentArea, 2);
        
        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(selectedComment);
                selectedComment.setDevice(device);
                commentService.saveComment(selectedComment);
                commentsGrid.setItems(commentService.getCommentsByDeviceId(device.getDeviceId()));
                dialog.close();
                showNotification(isNew ? "Comment created successfully" : "Comment updated successfully", 
                    NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error saving comment: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
    
    private void confirmDelete(Device device) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");
        
        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Are you sure you want to delete device: " + device.getName() + "?");
        textLayout.add("All comments associated with this device will also be deleted.");
        textLayout.setPadding(false);
        
        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                deviceService.deleteDevice(device.getDeviceId());
                updateList();
                confirmDialog.close();
                showNotification("Device deleted successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error deleting device: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
    
    private void confirmDeleteComment(DeviceComment comment, Grid<DeviceComment> commentsGrid) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");
        
        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Are you sure you want to delete this comment?");
        textLayout.setPadding(false);
        
        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                Long deviceId = comment.getDevice().getDeviceId();
                commentService.deleteComment(comment.getId());
                commentsGrid.setItems(commentService.getCommentsByDeviceId(deviceId));
                confirmDialog.close();
                showNotification("Comment deleted successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error deleting comment: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
            grid.setItems(deviceService.getAllDevices());
        } else {
            grid.setItems(deviceService.getAllDevices().stream()
                .filter(device -> 
                    (device.getName() != null && device.getName().toLowerCase().contains(filterValue.toLowerCase())) ||
                    (device.getModel() != null && device.getModel().toLowerCase().contains(filterValue.toLowerCase()))
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
