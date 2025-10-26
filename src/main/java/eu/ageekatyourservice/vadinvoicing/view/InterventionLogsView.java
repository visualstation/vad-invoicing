package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import eu.ageekatyourservice.vadinvoicing.model.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.service.InterventionLogService;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Route(value = "logs", layout = MainLayout.class)
@PageTitle("Intervention Logs | VAD Invoicing")
@PermitAll
public class InterventionLogsView extends VerticalLayout {
    
    private final InterventionLogService logService;
    private final Grid<InterventionLog> grid = new Grid<>(InterventionLog.class, false);
    private final TextField filterText = new TextField();
    
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public InterventionLogsView(InterventionLogService logService, AuthenticationContext authenticationContext) {
        this.logService = logService;
        
        addClassName("intervention-logs-view");
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
        filterText.setPlaceholder("Filter by username or description...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("400px");
        
        Button addButton = new Button("Add Log", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openLogDialog(null));
        
        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> updateList());
        
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton, refreshButton);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.START);
        
        return toolbar;
    }
    
    private void configureGrid() {
        grid.addClassName("intervention-log-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        
        grid.addColumn(log -> log.getTimestamp().format(DATE_FORMATTER))
            .setHeader("Date & Time")
            .setSortable(true)
            .setWidth("180px")
            .setFlexGrow(0);
        
        grid.addColumn(InterventionLog::getClientId)
            .setHeader("Client ID")
            .setSortable(true)
            .setWidth("130px")
            .setFlexGrow(0);
        
        grid.addColumn(InterventionLog::getUsername)
            .setHeader("Username")
            .setSortable(true)
            .setWidth("200px")
            .setFlexGrow(0);
        
        grid.addColumn(InterventionLog::getDescription)
            .setHeader("Description")
            .setSortable(true)
            .setAutoWidth(true)
            .setFlexGrow(1);
        
        grid.addColumn(InterventionLog::getDuration)
            .setHeader("Duration (s)")
            .setSortable(true)
            .setWidth("120px")
            .setFlexGrow(0);
        
        grid.addColumn(InterventionLog::getBilledDuration)
            .setHeader("Billed (s)")
            .setSortable(true)
            .setWidth("110px")
            .setFlexGrow(0);
        
        grid.addComponentColumn(log -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openLogDialog(log));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(log));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions").setWidth("130px").setFlexGrow(0);
    }
    
    private void openLogDialog(InterventionLog log) {
        Dialog dialog = new Dialog();
        dialog.setWidth("700px");
        
        boolean isNew = log == null;
        InterventionLog formBean = isNew ? new InterventionLog() : log;
        
        H2 title = new H2(isNew ? "Add New Intervention Log" : "Edit Intervention Log");
        
        // Create form fields
        DateTimePicker timestampField = new DateTimePicker("Timestamp");
        timestampField.setRequiredIndicatorVisible(true);
        timestampField.setWidthFull();
        if (isNew) {
            timestampField.setValue(LocalDateTime.now());
        }
        
        TextField clientIdField = new TextField("Client ID");
        clientIdField.setRequired(true);
        clientIdField.setWidthFull();
        
        TextField usernameField = new TextField("Username");
        usernameField.setRequired(true);
        usernameField.setWidthFull();
        
        TextArea descriptionArea = new TextArea("Description");
        descriptionArea.setRequired(true);
        descriptionArea.setWidthFull();
        descriptionArea.setMaxLength(1000);
        descriptionArea.setMinHeight("100px");
        
        IntegerField durationField = new IntegerField("Duration (seconds)");
        durationField.setRequired(true);
        durationField.setMin(0);
        durationField.setWidthFull();
        
        IntegerField billedDurationField = new IntegerField("Billed Duration (seconds)");
        billedDurationField.setRequired(true);
        billedDurationField.setMin(0);
        billedDurationField.setWidthFull();
        
        // Create binder
        Binder<InterventionLog> binder = new BeanValidationBinder<>(InterventionLog.class);
        binder.forField(timestampField)
            .asRequired("Timestamp is required")
            .bind(InterventionLog::getTimestamp, InterventionLog::setTimestamp);
        binder.forField(clientIdField)
            .asRequired("Client ID is required")
            .bind(InterventionLog::getClientId, InterventionLog::setClientId);
        binder.forField(usernameField)
            .asRequired("Username is required")
            .bind(InterventionLog::getUsername, InterventionLog::setUsername);
        binder.forField(descriptionArea)
            .asRequired("Description is required")
            .bind(InterventionLog::getDescription, InterventionLog::setDescription);
        binder.forField(durationField)
            .asRequired("Duration is required")
            .bind(InterventionLog::getDuration, InterventionLog::setDuration);
        binder.forField(billedDurationField)
            .asRequired("Billed duration is required")
            .bind(InterventionLog::getBilledDuration, InterventionLog::setBilledDuration);
        
        binder.readBean(formBean);
        
        // Create form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(timestampField, clientIdField, usernameField, durationField, billedDurationField, descriptionArea);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(timestampField, 2);
        formLayout.setColspan(descriptionArea, 2);
        
        // Create buttons
        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(formBean);
                logService.saveInterventionLog(formBean);
                updateList();
                dialog.close();
                showNotification(isNew ? "Log created successfully" : "Log updated successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error saving log: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
    
    private void confirmDelete(InterventionLog log) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");
        
        H2 title = new H2("Confirm Deletion");
        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add("Are you sure you want to delete this intervention log?");
        textLayout.setPadding(false);
        
        Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            try {
                logService.deleteInterventionLog(log.getId());
                updateList();
                confirmDialog.close();
                showNotification("Log deleted successfully", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error deleting log: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
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
            grid.setItems(logService.findAllInterventionLogs());
        } else {
            grid.setItems(logService.findAllInterventionLogs().stream()
                .filter(log -> 
                    log.getUsername().toLowerCase().contains(filterValue.toLowerCase()) ||
                    log.getDescription().toLowerCase().contains(filterValue.toLowerCase())
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
