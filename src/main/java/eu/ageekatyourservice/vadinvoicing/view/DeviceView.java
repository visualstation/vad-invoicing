package eu.ageekatyourservice.vadinvoicing.view;

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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import eu.ageekatyourservice.vadinvoicing.entity.Customer;
import eu.ageekatyourservice.vadinvoicing.entity.Device;
import eu.ageekatyourservice.vadinvoicing.entity.DeviceComment;
import eu.ageekatyourservice.vadinvoicing.service.CustomerService;
import eu.ageekatyourservice.vadinvoicing.service.DeviceCommentService;
import eu.ageekatyourservice.vadinvoicing.service.DeviceService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@Route(value = "devices", layout = MainLayout.class)
@PageTitle("Devices | VAD Invoicing")
@PermitAll
public class DeviceView extends VerticalLayout {

    private final DeviceService deviceService;
    private final DeviceCommentService commentService;
    private final CustomerService customerService;

    private final Grid<Device> deviceGrid = new Grid<>(Device.class, false);
    private final Grid<DeviceComment> commentGrid = new Grid<>(DeviceComment.class, false);

    private final TextField filterText = new TextField();

    private Device selectedDevice;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public DeviceView(DeviceService deviceService,
                      DeviceCommentService commentService,
                      CustomerService customerService,
                      AuthenticationContext authenticationContext) {
        this.deviceService = deviceService;
        this.commentService = commentService;
        this.customerService = customerService;

        addClassName("device-view");
        setSizeFull();
        setPadding(true);

        configureDeviceGrid();
        configureCommentGrid();

        add(
            createToolbar(),
            deviceGrid,
            new H2("Comments for selected device"),
            commentGrid
        );

        updateDeviceList();
    }

    private HorizontalLayout createToolbar() {
        filterText.setPlaceholder("Filter by label or id...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateDeviceList());
        filterText.setWidth("400px");

        Button addButton = new Button("Add Device", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openDeviceDialog(null));

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> updateDeviceList());

        Button addCommentBtn = new Button("Add Comment", new Icon(VaadinIcon.COMMENT));
        addCommentBtn.addClickListener(e -> openCommentDialog(null));
        addCommentBtn.setEnabled(false);

        deviceGrid.addSelectionListener(ev -> {
            selectedDevice = ev.getFirstSelectedItem().orElse(null);
            addCommentBtn.setEnabled(selectedDevice != null);
            refreshComments();
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, refreshButton, addCommentBtn);
        toolbar.setAlignItems(Alignment.CENTER);

        return toolbar;
    }

    private void configureDeviceGrid() {
        deviceGrid.addClassName("device-grid");
        deviceGrid.setSizeFull();
        deviceGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        deviceGrid.addColumn(Device::getId)
            .setHeader("Device ID")
            .setSortable(true)
            .setWidth("150px")
            .setFlexGrow(0);

        deviceGrid.addColumn(Device::getAlias)
            .setHeader("Alias")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);

        deviceGrid.addColumn(Device::getLabel)
            .setHeader("Label")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);

        deviceGrid.addColumn(d -> d.getCustomer() != null ? d.getCustomer().getName() : "N/A")
            .setHeader("Customer")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);

        deviceGrid.addComponentColumn(device -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openDeviceDialog(device));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDeleteDevice(device));

            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions").setWidth("130px").setFlexGrow(0);
    }

    private void configureCommentGrid() {
        commentGrid.addClassName("comment-grid");
        commentGrid.setSizeFull();
        commentGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        commentGrid.addColumn(c -> c.getCreatedAt().format(DATE_FORMATTER))
            .setHeader("Created At")
            .setSortable(true)
            .setWidth("180px")
            .setFlexGrow(0);

        commentGrid.addColumn(DeviceComment::getText)
            .setHeader("Comment")
            .setAutoWidth(true)
            .setFlexGrow(1);

        commentGrid.addComponentColumn(comment -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openCommentDialog(comment));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDeleteComment(comment));

            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions").setWidth("130px").setFlexGrow(0);
    }

    private void openDeviceDialog(Device device) {
        Dialog dialog = new Dialog();
        dialog.setWidth("700px");

        boolean isNew = device == null;
        Device formBean = isNew ? new Device() : device;

        H2 title = new H2(isNew ? "Add New Device" : "Edit Device");

        NumberField idField = new NumberField("Device ID (9-10 digits)");
        idField.setMin(100000000d);
        idField.setMax(9999999999d);
        idField.setStep(1);
        idField.setClearButtonVisible(true);
        idField.setWidthFull();
        idField.setEnabled(isNew); // id is PK, not editable when editing

        TextField aliasField = new TextField("Alias");
        aliasField.setWidthFull();

        TextField labelField = new TextField("Label");
        labelField.setWidthFull();

        TextArea commentArea = new TextArea("Comment");
        commentArea.setWidthFull();
        commentArea.setMinHeight("100px");
        commentArea.setMaxLength(2000);

        // Customer selection - now optional
        com.vaadin.flow.component.combobox.ComboBox<Customer> customerCombo = new com.vaadin.flow.component.combobox.ComboBox<>("Customer (Optional)");
        customerCombo.setItems(customerService.getAllCustomers());
        customerCombo.setItemLabelGenerator(Customer::getName);
        customerCombo.setWidthFull();
        customerCombo.setClearButtonVisible(true);

        Binder<Device> binder = new BeanValidationBinder<>(Device.class);
        binder.forField(idField).withValidator(val -> val != null && val >= 100000000d && val <= 9999999999d,
                "ID must be 9 to 10 digits").bind(
                d -> d.getId() == null ? null : d.getId().doubleValue(),
                (d, val) -> d.setId(val == null ? null : val.longValue())
        );
        binder.bind(aliasField, Device::getAlias, Device::setAlias);
        binder.bind(labelField, Device::getLabel, Device::setLabel);
        binder.bind(commentArea, Device::getComment, Device::setComment);
        binder.bind(customerCombo, Device::getCustomer, Device::setCustomer);

        binder.readBean(formBean);

        FormLayout formLayout = new FormLayout();
        formLayout.add(idField, aliasField, labelField, customerCombo);
        formLayout.add(commentArea, 2);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(formBean);
                deviceService.save(formBean);
                updateDeviceList();
                dialog.close();
                showNotification(isNew ? "Device created" : "Device updated", NotificationVariant.LUMO_SUCCESS);
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

    private void openCommentDialog(DeviceComment comment) {
        if (selectedDevice == null) {
            showNotification("Select a device first", NotificationVariant.LUMO_PRIMARY);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        boolean isNew = comment == null;
        DeviceComment formBean = isNew ? new DeviceComment() : comment;
        if (isNew) {
            formBean.setDevice(selectedDevice);
        }

        H2 title = new H2(isNew ? "Add Comment" : "Edit Comment");

        TextArea textArea = new TextArea("Comment");
        textArea.setWidthFull();
        textArea.setMinHeight("120px");
        textArea.setMaxLength(2000);

        Binder<DeviceComment> binder = new BeanValidationBinder<>(DeviceComment.class);
        binder.forField(textArea).asRequired("Comment is required")
                .bind(DeviceComment::getText, DeviceComment::setText);

        binder.readBean(formBean);

        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(formBean);
                commentService.save(formBean);
                refreshComments();
                dialog.close();
                showNotification(isNew ? "Comment added" : "Comment updated", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error saving comment: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
        cancelButton.addClickListener(e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(title, textArea, new HorizontalLayout(saveButton, cancelButton));
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void confirmDeleteDevice(Device device) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Delete device: " + device.getId() + "?");
        textLayout.setPadding(false);

        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                deviceService.delete(device.getId());
                updateDeviceList();
                confirmDialog.close();
                showNotification("Device deleted", NotificationVariant.LUMO_SUCCESS);
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

    private void confirmDeleteComment(DeviceComment comment) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Delete this comment?");
        textLayout.setPadding(false);

        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                commentService.delete(comment.getId());
                refreshComments();
                confirmDialog.close();
                showNotification("Comment deleted", NotificationVariant.LUMO_SUCCESS);
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

    private void updateDeviceList() {
        String filterValue = filterText.getValue();
        if (filterValue == null || filterValue.isEmpty()) {
            deviceGrid.setItems(deviceService.getAll());
        } else {
            deviceGrid.setItems(deviceService.getAll().stream()
                    .filter(d ->
                            (d.getLabel() != null && d.getLabel().toLowerCase().contains(filterValue.toLowerCase())) ||
                            (d.getAlias() != null && d.getAlias().toLowerCase().contains(filterValue.toLowerCase())) ||
                            (String.valueOf(d.getId()).contains(filterValue))
                    ).toList());
        }
        refreshComments();
    }

    private void refreshComments() {
        if (selectedDevice == null) {
            commentGrid.setItems();
        } else {
            commentGrid.setItems(commentService.findByDevice(selectedDevice));
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
