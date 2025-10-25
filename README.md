# VAD Invoicing System

A professional web-based invoicing and device management system built with Vaadin and Spring Boot.

## Features

### Professional Interface
- **Left Sidebar Navigation**: Clean, organized menu structure with icons
- **Dashboard**: Overview page with statistics cards showing total customers, devices, and intervention logs
- **Responsive Design**: Works on desktop and mobile devices
- **Modern Theme**: Professional styling with custom CSS and Vaadin Lumo theme

### Entity Management

#### Customers
- Create, Read, Update, Delete (CRUD) operations
- Fields: Name, Email, Phone, Contact Person, Website, Address, Billing Rate
- Search and filter by name or email
- Form validation

#### Devices
- Full CRUD operations with device management
- 9-10 digit unique device IDs
- Associate devices with customers
- Optional labels for easy identification
- Device comments system with timestamps

#### Intervention Logs
- Track interventions with detailed information
- Fields: Timestamp, Client ID, Username, Description, Duration, Billed Duration
- Filter by username or description
- Date-time picker for accurate timestamp entry

### Database Persistence
- All entities automatically persist to H2 database
- JPA/Hibernate ORM for data management
- Automatic schema generation
- Transaction management by Spring

### Security
- Form-based authentication
- Protected routes (all pages except login)
- Logout functionality
- Default admin account

## Technology Stack

- **Frontend**: Vaadin 24.9.3
- **Backend**: Spring Boot 3.5.7
- **Database**: H2 (in-memory)
- **Java**: 17
- **Build**: Maven
- **ORM**: JPA/Hibernate

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using your local Maven
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Default Login Credentials

```
Username: admin
Password: admin
```

### H2 Database Console

Access the H2 console at `http://localhost:8080/h2-console`

**Connection details:**
- JDBC URL: `jdbc:h2:mem:logdb`
- Username: `sa`
- Password: (leave blank)

## Project Structure

```
src/main/java/eu/ageekatyourservice/vadinvoicing/
├── entity/               # JPA entities
│   ├── Customer.java
│   ├── Device.java
│   ├── DeviceComment.java
│   ├── InterventionLog.java
│   └── User.java
├── repository/          # JPA repositories
├── service/             # Business logic layer
├── view/                # Vaadin UI views
│   ├── MainLayout.java       # Sidebar layout
│   ├── MainView.java         # Dashboard
│   ├── CustomerView.java     # Customer management
│   ├── DeviceView.java       # Device management
│   ├── InterventionLogsView.java  # Log management
│   └── LoginView.java        # Login page
├── security/            # Spring Security configuration
├── task/                # Background tasks
└── VadInvoicingApplication.java

src/main/resources/
├── META-INF/resources/themes/vad-invoicing/
│   ├── styles.css       # Custom styles
│   └── theme.json       # Theme configuration
└── application.properties
```

## Key Features Implementation

### Sidebar Navigation
The `MainLayout` provides a consistent navigation experience across all views with:
- Dashboard link
- Customer management
- Device management  
- Intervention logs
- Logout button in header

### Database Persistence
All entities are properly configured for database persistence:

1. **Entities**: Annotated with `@Entity` and proper JPA mappings
2. **Repositories**: Extend `JpaRepository` for database operations
3. **Services**: Use repository methods to save/retrieve data
4. **Views**: Bind forms to entities and call service methods

Example flow for creating a customer:
```
CustomerView (form) → CustomerService.saveCustomer() → CustomerRepository.save() → Database
```

### Form Validation
- Required fields marked with asterisk (*)
- Client-side validation before submission
- Server-side validation with Bean Validation
- User-friendly error messages

### CRUD Operations

All entities support full CRUD operations:
- **Create**: Dialog forms with validation
- **Read**: Grid displays with sorting and filtering
- **Update**: Edit dialogs pre-populated with existing data
- **Delete**: Confirmation dialogs before deletion

## Customization

### Theme
Modify `/src/main/resources/META-INF/resources/themes/vad-invoicing/styles.css` to customize colors, fonts, and styling.

### Adding New Entities
1. Create entity class in `entity/` package
2. Create repository interface in `repository/`
3. Create service class in `service/`
4. Create view in `view/` with CRUD operations
5. Add navigation link in `MainLayout`

## Development

### Building
```bash
./mvnw clean install
```

### Running Tests
```bash
./mvnw test
```

### Production Build
```bash
./mvnw clean package -Pproduction
```

## Database Configuration

By default, uses H2 in-memory database. To use a persistent database:

1. Update `application.properties`
2. Add database driver dependency to `pom.xml`
3. Configure connection details

Example for PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vadinvoicing
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## Support

For issues or questions, please refer to:
- [Vaadin Documentation](https://vaadin.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## License

This project is part of the VAD Invoicing system.
