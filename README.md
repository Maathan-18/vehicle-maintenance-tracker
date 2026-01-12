# Smart Vehicle Maintenance Tracker

A full-stack web application for tracking vehicle maintenance built with Spring Boot and Thymeleaf.

## 🚀 Features

- **User Authentication**: Secure registration and login with Spring Security
- **Vehicle Management**: Add, edit, and delete vehicles with details like make, model, fuel type
- **Service Records**: Log maintenance activities with cost tracking
- **Dashboard Analytics**: Charts showing monthly costs and cost breakdown by category
- **Automated Reminders**: Spring Scheduler generates service reminders based on date/odometer

## 🛠️ Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.2.1 |
| Database | MySQL with Hibernate/JPA |
| Frontend | Thymeleaf + Bootstrap 5 |
| Security | Spring Security + BCrypt |
| Charts | Chart.js |
| Build | Maven |

## 📚 Syllabus Concepts Demonstrated

- **Java 8+ Features**: Stream API, Lambda Expressions, Method References
- **OOP Principles**: Encapsulation, Inheritance, Polymorphism
- **Custom Exceptions**: VehicleNotFoundException with @ControllerAdvice
- **Spring Scheduler**: @Scheduled cron jobs for automated reminders
- **JPA Relationships**: @OneToMany, @ManyToOne with bidirectional mapping

## 📦 Prerequisites

- Java 17+
- MySQL 8.x
- Maven 3.9+

## ⚙️ Configuration

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_tracker_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

## 🏃 Running the Application

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/vehicle-maintenance-tracker.git

# Navigate to project directory
cd vehicle-maintenance-tracker

# Run with Maven wrapper
./mvnw spring-boot:run
```

Open browser: http://localhost:8080

## 📁 Project Structure

```
src/main/java/com/luminar/tracker/
├── config/          # Security configuration
├── controller/      # MVC controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exceptions
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic with Java Streams
```

## 📸 Screenshots

### Login Page
Modern glassmorphism design with gradient background

### Dashboard
Analytics with Chart.js showing monthly costs and category breakdown

### Vehicle Management
Card-based layout with fuel type badges and odometer tracking

## 👤 Author

**Maathan** - Java Full Stack Development

## 📄 License

This project is for educational purposes.
