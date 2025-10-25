package eu.ageekatyourservice.vadinvoicing.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.service.InterventionLogService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@Route(value = "home", layout = MainLayout.class)
@PageTitle("Intervention Logs")
@PermitAll
public class HomepageView extends VerticalLayout {
    private final InterventionLogService logService;
    private final AuthenticationContext authenticationContext;
    private final Grid<InterventionLog> grid = new Grid<>(InterventionLog.class, false);
    private final TextField filterText = new TextField();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public HomepageView(InterventionLogService logService, AuthenticationContext authenticationContext) {
        this.logService = logService;
        this.authenticationContext = authenticationContext;

        addClassName("main-view");
        setSizeFull();

        configureGrid();

        add(
                createToolbar(),
                grid
        );

        updateList();
    }

    private HorizontalLayout createHeader() { return new HorizontalLayout(); }

    private HorizontalLayout createToolbar() {
        filterText.setPlaceholder("Filter by username or description...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidth("400px");

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, refreshButton);
        toolbar.setAlignItems(Alignment.CENTER);

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
    }

    private void updateList() {
        String filterValue = filterText.getValue();

        if (filterValue == null || filterValue.isEmpty()) {
            grid.setItems(logService.getAllLogs());
        } else {
            // Simple filter: search in username and description
            grid.setItems(logService.getAllLogs().stream()
                    .filter(log ->
                            log.getUsername().toLowerCase().contains(filterValue.toLowerCase()) ||
                                    log.getDescription().toLowerCase().contains(filterValue.toLowerCase())
                    )
                    .toList()
            );
        }
    }
}
