# Product Requirement Document (PRD)
## MAINTENANCETRACKER - Smart Vehicle Maintenance Tracker

**Version:** 2.0  
**Date:** January 2026  
**Author:** Development Team  
**Status:** ✅ MVP Implemented  

---

## Implementation Status

| Feature | Status | Notes |
|---------|--------|-------|
| User Authentication | ✅ Complete | Spring Security + BCrypt |
| Vehicle Management | ✅ Complete | CRUD with validation |
| Service Records | ✅ Complete | With payment method tracking |
| Service History Page | ✅ Complete | Dedicated `/services` page |
| Dashboard Analytics | ✅ Complete | Chart.js integration |
| Automated Reminders | ✅ Complete | Spring Scheduler `@Scheduled` |
| Custom Exceptions | ✅ Complete | `@ControllerAdvice` |
| Java Streams Demo | ✅ Complete | Cost calculations |

---

## 1. Executive Summary

The **MAINTENANCETRACKER** is a full-stack web application designed to help vehicle owners efficiently manage maintenance schedules, track service history, monitor costs, and receive timely service reminders. The system enables users to maintain records for multiple vehicles and gain insights through analytics dashboards.

**Target Users:** Individual vehicle owners (motorcycles, cars, scooters)  
**Timeline:** 1 week development  
**Tech Stack:** 
- **Backend:** Spring Boot 3.2.1, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf + Bootstrap 5 + Chart.js
- **Database:** MySQL 8.x with Hibernate ORM
- **Scheduler:** Spring Scheduler for automated reminders

---

## 2. Product Overview

### 2.1 Vision
Empower vehicle owners with a centralized, intelligent platform to never miss a scheduled maintenance, track maintenance costs, and make informed decisions about their vehicles' upkeep.

### 2.2 Core Value Proposition
- **Automated Reminders:** Never miss a service due date or mileage milestone.
- **Cost Tracking:** Understand exactly how much you spend on vehicle maintenance.
- **Service History:** Maintain a complete, searchable maintenance log.
- **Smart Insights:** Dashboard analytics showing trends, costs, and patterns.
- **Service Center Directory:** Find and rate service centers; learn from community feedback.

### 2.3 Success Metrics
- Users can log in and register a vehicle in < 5 minutes.
- Service reminders trigger 7 days before due date.
- Users can generate monthly/yearly cost reports.
- 95%+ uptime for API endpoints.
- Clean, intuitive UI with <3 clicks to perform core actions.

---

## 3. User Stories & Features

### 3.1 User Authentication & Profile Management

**US-101: User Registration**
- **As a** new user,  
- **I want to** create an account with email and password,  
- **So that** I can use the vehicle maintenance tracker.

**Acceptance Criteria:**
- User provides email, password, confirm password, name, and phone.
- Email validation (RFC 5322 compliant).
- Password strength requirement: min 8 chars, 1 uppercase, 1 number, 1 special char.
- Duplicate email prevention.
- Success: user account created, verification email sent (optional for MVP).
- Error: validation messages displayed.

**US-102: User Login**
- **As a** registered user,  
- **I want to** log in with email and password,  
- **So that** I can access my vehicles and maintenance records.

**Acceptance Criteria:**
- Email and password validation.
- Session management via JWT tokens (or session cookies).
- "Remember me" option (optional).
- Redirect to dashboard on success.
- Error handling for incorrect credentials.

**US-103: User Profile Management**
- **As a** logged-in user,  
- **I want to** view and edit my profile (name, email, phone, address),  
- **So that** I can keep my information up to date.

**Acceptance Criteria:**
- View profile form pre-populated with current data.
- Edit and save changes.
- Password change functionality.
- Success notification.

---

### 3.2 Vehicle Management

**US-201: Add Vehicle**
- **As a** logged-in user,  
- **I want to** register a new vehicle,  
- **So that** I can start tracking its maintenance.

**Acceptance Criteria:**
- Form fields:
  - Registration number (unique per user, alphanumeric validation).
  - Make (dropdown or text).
  - Model (dropdown or text).
  - Variant (optional text).
  - Year (year picker, 1990 onwards).
  - Engine capacity (cc, numeric).
  - Fuel type (Petrol, Diesel, CNG, Electric—dropdown).
  - Purchase date (date picker).
  - Current odometer (km, numeric).
  - Notes (optional text area).
- Upload RC/Insurance copy (optional, max 5MB, PDF/JPG/PNG).
- Validation: all required fields mandatory.
- Success: vehicle added to user's vehicle list.
- Error: show validation errors.

**US-202: View Vehicle List**
- **As a** logged-in user,  
- **I want to** see all my registered vehicles,  
- **So that** I can manage and monitor them.

**Acceptance Criteria:**
- Display list with: registration number, make, model, year, current odometer, last service date.
- Sort options: by registration, by date added, by last service.
- Search by registration number or make.
- Action buttons: View Details, Edit, Delete.
- If no vehicles, show "No vehicles registered" + link to add.

**US-203: View Vehicle Details**
- **As a** logged-in user,  
- **I want to** see detailed information about a specific vehicle,  
- **So that** I can review its maintenance history and stats.

**Acceptance Criteria:**
- Display all vehicle fields.
- Show total maintenance cost to date.
- Show next scheduled service (date and km).
- Quick stats: number of services, average cost per service.
- Link to full maintenance history.
- Option to download RC/Insurance if uploaded.

**US-204: Edit Vehicle**
- **As a** logged-in user,  
- **I want to** update vehicle information,  
- **So that** I can keep records current (e.g., update odometer).

**Acceptance Criteria:**
- Pre-fill form with current data.
- Allow edit of: current odometer, notes, purchase date.
- Prevent edit of: registration number, make, model, year (or require confirmation).
- Save changes with success notification.

**US-205: Delete Vehicle**
- **As a** logged-in user,  
- **I want to** remove a vehicle from my account,  
- **So that** I can clean up unused entries.

**Acceptance Criteria:**
- Confirmation dialog: "Delete vehicle and all associated records?"
- On confirm: soft delete or hard delete with cascade.
- Success notification.
- Redirect to vehicle list.

---

### 3.3 Service & Maintenance Tracking

**US-301: Log Service/Maintenance**
- **As a** logged-in user,  
- **I want to** add a maintenance record for a vehicle,  
- **So that** I can track what services have been done and their costs.

**Acceptance Criteria:**
- Form fields (required unless noted):
  - Vehicle (dropdown, pre-selected if coming from vehicle detail).
  - Service date (date picker, default today).
  - Odometer reading (numeric, km).
  - Service type (dropdown: Oil Change, Filter Replacement, Chain Lube, Tyre Service, Battery Replacement, Brake Service, Suspension, General Service, Other).
  - Description (optional text area, e.g., "Replaced oil with Mobil Fully Synthetic").
  - Total cost (numeric, currency).
  - **Payment method (dropdown: Cash, Card, UPI, Net Banking).** ✅ *Implemented*
  - Service center name (text input).
  - Next service due—date (optional, date picker).
  - Next service due—odometer (optional, numeric, km).
- Validation: all required fields, odometer >= last recorded odometer (or warn user).
- On save: auto-calculate "next service due" if not provided (e.g., +3000 km or +6 months from last service).
- Success: redirect to vehicle detail or service history page.

**US-302: View Maintenance History**
- **As a** logged-in user,  
- **I want to** see a list of all services for a vehicle,  
- **So that** I can review what has been done and when.

**Acceptance Criteria:**
- Display table with columns: Date, Odometer, Service Type, Description, Cost, Service Center, Receipt link.
- Sort by date (newest first, with toggle).
- Sort by odometer reading.
- Filter by service type.
- Pagination: 10 records per page.
- View detail / Edit / Delete actions.
- Show total cost for all records in this list.

**US-303: Edit Service Record**
- **As a** logged-in user,  
- **I want to** modify a maintenance record,  
- **So that** I can correct errors or update information.

**Acceptance Criteria:**
- Pre-fill form with current data.
- Allow edit of all fields.
- Validate odometer (should not decrease below previous/next record odometer).
- Save with success notification.
- Optionally auto-recalculate "next service due" if odometer or service type changed.

**US-304: Delete Service Record**
- **As a** logged-in user,  
- **I want to** remove a service record,  
- **So that** I can delete erroneous or unwanted entries.

**Acceptance Criteria:**
- Confirmation dialog.
- On confirm: delete record (and associated receipt file if any).
- Success notification.
- Refresh maintenance history list.

**US-305: Upload Receipt**
- **As a** logged-in user,  
- **I want to** attach a receipt/bill to a service record,  
- **So that** I can have proof of service and expense.

**Acceptance Criteria:**
- Upload during service creation or edit.
- Allowed formats: PDF, JPG, PNG.
- Max file size: 5MB.
- Store file: locally in `uploads/receipts/{userId}/{vehicleId}/{serviceId}/` or AWS S3.
- Display download link on maintenance history.
- Optional: thumbnail preview for images.
- On service deletion: delete associated file(s).

---

### 3.4 Service Reminders (Scheduled)

**US-401: Automatic Service Reminders**
- **As a** vehicle owner,  
- **I want to** receive notifications about upcoming services,  
- **So that** I don't miss scheduled maintenance.

**Acceptance Criteria:**
- Backend scheduler job runs daily (or every 6 hours).
- Job logic:
  - For each vehicle, check `nextServiceDate` and `nextServiceOdometer`.
  - If `nextServiceDate <= today + 7 days`, create reminder.
  - If current odometer is within 500 km of `nextServiceOdometer`, create reminder.
- Store reminders in `Reminder` table with: vehicle_id, reminder_type (DATE_BASED / KM_BASED), due_date, due_km, is_read, created_at.
- Reminders are viewable in dashboard and sent via email (optional).
- User can mark reminders as read/dismiss.

**US-402: View Upcoming Services**
- **As a** logged-in user,  
- **I want to** see a list of upcoming services across all my vehicles,  
- **So that** I can plan maintenance visits.

**Acceptance Criteria:**
- Dashboard widget: "Upcoming Services" showing next 5 services.
- Display: vehicle (registration), service type (inferred from last service), due date, days until due, due km.
- Mark as "Completed" when user adds service record or dismisses.
- Sort by due date (earliest first).
- Link to "Add Service" form for that vehicle.

**US-403: Manual Reminder Management**
- **As a** logged-in user,  
- **I want to** view and manage reminders,  
- **So that** I can keep track of what's due and what's not.

**Acceptance Criteria:**
- Reminder list page showing all reminders (active and dismissed).
- Toggle to show only active reminders.
- Action: "Mark as Read", "Dismiss", or "Mark as Done" (when user logs service).
- Filter by vehicle.
- Pagination: 20 reminders per page.

---

### 3.5 Service Center Management

**US-501: Add Service Center**
- **As a** logged-in user,  
- **I want to** register a service center,  
- **So that** I can associate services with specific repair shops and track them.

**Acceptance Criteria:**
- Form fields (required unless noted):
  - Name (text, max 100 chars).
  - Address (text area, max 500 chars).
  - Phone number (10-digit Indian phone, validate).
  - Email (optional, RFC 5322 validation).
  - Service type (checkbox: Authorized Dealer, Local Mechanic, Both).
  - Specialization (checkbox: Cars, Motorcycles, Scooters, All).
  - Website (optional, URL validation).
  - Notes (optional text area).
- Validation: name and phone mandatory.
- Success: service center added to user's list.
- Optionally: auto-geocode address to latitude/longitude for future map feature.

**US-502: View Service Center Directory**
- **As a** logged-in user,  
- **I want to** see all service centers I've added or reviewed,  
- **So that** I can choose where to service my vehicle.

**Acceptance Criteria:**
- Display list with columns: Name, Address, Phone, Type, Avg Rating, Number of reviews.
- Sort by: name, rating (highest first), most reviews.
- Search by name or address.
- Filter by type (Authorized / Local Mechanic).
- Filter by specialization.
- Pagination: 10 per page.
- Action buttons: View Details, Edit, Add Review, Delete.

**US-503: View Service Center Details**
- **As a** logged-in user,  
- **I want to** see detailed information and reviews for a service center,  
- **So that** I can decide if I want to use it.

**Acceptance Criteria:**
- Display all service center fields.
- Show all reviews: author (user name), date, rating (stars), comment.
- Calculate and display average rating and total reviews.
- Show list of services I've used at this center (dates, vehicles, costs).
- Option to add new review.
- Option to edit or delete center (if user is owner).

**US-504: Add Review & Rating**
- **As a** logged-in user,  
- **I want to** leave a review and rating for a service center,  
- **So that** other users can benefit from my experience.

**Acceptance Criteria:**
- Form fields:
  - Rating (1–5 stars, required, displayed as clickable stars).
  - Comment (optional text area, max 500 chars).
  - Recommend? (optional checkbox, Yes/No).
- Validation: rating required.
- One review per user per service center (or allow edit of existing).
- On save: redirect to service center detail, show success message.
- Display average rating and review count updated.

**US-505: Edit Service Center**
- **As a** the user who added a service center,  
- **I want to** update its information,  
- **So that** I can keep details current.

**Acceptance Criteria:**
- Pre-fill form with current data.
- Allow edit of all fields.
- Validation same as add.
- Save with success notification.

**US-506: Delete Service Center**
- **As a** the user who added a service center,  
- **I want to** remove it from my directory,  
- **So that** I can clean up unused entries.

**Acceptance Criteria:**
- Confirmation dialog: "Delete service center?"
- Option: delete only the center, or delete center + all associated reviews by this user.
- Services already logged to this center are not deleted, but center reference becomes "Unknown" or null.
- Success notification.

---

### 3.6 Analytics & Dashboard

**US-601: Dashboard Overview**
- **As a** logged-in user,  
- **I want to** see a summary dashboard on login,  
- **So that** I can quickly understand my vehicles' status and costs at a glance.

**Acceptance Criteria:**
- Dashboard displays (all responsive, mobile-friendly):
  1. **Summary Cards:**
     - Total vehicles count.
     - Total maintenance cost (all time, or last 12 months toggle).
     - Upcoming services count (due within 30 days).
     - Number of service centers.
  2. **Upcoming Services Widget:**
     - List of next 5 services with due date, vehicle, type.
     - Link to add service.
  3. **Monthly Cost Trend Chart:**
     - Line chart: last 12 months, showing total maintenance cost per month.
     - Clickable data points to drill down.
  4. **Cost by Category Pie Chart:**
     - Breakdown of costs by service type (Oil Change, Tyres, Battery, etc.) for selected period.
  5. **Most Expensive Vehicles:**
     - Bar chart or table: top 3 vehicles by total maintenance cost.
  6. **Quick Links:**
     - Add vehicle, add service, view reminders, service centers, analytics.

**US-602: Monthly Cost Report**
- **As a** logged-in user,  
- **I want to** view and analyze maintenance costs by month/year,  
- **So that** I can budget and understand spending trends.

**Acceptance Criteria:**
- Report page with filters:
  - Date range (month/year picker or custom date range).
  - Vehicle filter (single or multi-select).
  - Service type filter (optional).
- Display:
  - Table: Date, Vehicle, Service Type, Cost, Service Center.
  - Subtotals by month.
  - Total for selected period.
- Download as CSV or PDF.
- Chart: cost trend over selected period.

**US-603: Vehicle-wise Cost Analysis**
- **As a** logged-in user,  
- **I want to** analyze costs for a specific vehicle,  
- **So that** I can understand if one vehicle is more expensive to maintain than others.

**Acceptance Criteria:**
- Report page, pre-filtered to a single vehicle.
- Displays:
  - Total cost to date.
  - Average cost per service.
  - Cost per month (average and chart).
  - Cost by service type (pie chart).
  - List of all services for this vehicle with cost breakdown.
- Comparison: show average vs this vehicle (optional, using data from all vehicles).

**US-604: Expense Analytics**
- **As a** logged-in user,  
- **I want to** generate detailed expense reports,  
- **So that** I can present data to family or for insurance/tax purposes.

**Acceptance Criteria:**
- Filters:
  - Date range (custom or presets: last 30 days, last quarter, last year, all time).
  - Vehicle(s).
  - Service type.
- Report displays:
  - Summary statistics: total, average, min, max, median.
  - Table with detailed records.
  - Charts: trend line, pie by category, bar by vehicle.
- Export options: CSV, PDF, Excel.
- Print-friendly layout.

**US-605: Reminders Analytics**
- **As a** logged-in user,  
- **I want to** see statistics on reminders and service compliance,  
- **So that** I can track if I'm maintaining my vehicles on schedule.

**Acceptance Criteria:**
- Display:
  - Total reminders created.
  - Reminders acted upon (service logged) vs dismissed.
  - Average time between reminder and service logged (in days).
  - Vehicles with most overdue services.
- Chart: reminders timeline (when created, when acted on).

---

### 3.7 Notifications & Alerts

**US-701: Email Notifications**
- **As a** a vehicle owner,  
- **I want to** receive email alerts about upcoming services,  
- **So that** I don't forget to service my vehicle.

**Acceptance Criteria:**
- On reminder creation, send email:
  - Subject: "[Vehicle Reg] Service Due Soon".
  - Body: vehicle name, service type (inferred), due date, due km, link to service center/add service.
- Email format: plain text + HTML.
- User can opt-out of email notifications in settings.
- Sent asynchronously (non-blocking).
- Log all emails sent (for debugging).

**US-702: In-App Notifications**
- **As a** a logged-in user,  
- **I want to** see notifications within the app,  
- **So that** I'm aware of important updates without leaving the page.

**Acceptance Criteria:**
- Toast/snackbar notifications for:
  - Service record added/updated/deleted.
  - Reminder dismissed/completed.
  - Vehicle added/updated.
- Notifications display for 5 seconds and auto-dismiss.
- Notification bell icon in header showing unread count.
- Notification history page showing last 50 notifications.

**US-703: Notification Preferences**
- **As a** a logged-in user,  
- **I want to** configure notification preferences,  
- **So that** I receive only the alerts I care about.

**Acceptance Criteria:**
- Settings page with toggles:
  - Email reminders on/off.
  - Reminder threshold (days before due date to notify, e.g., 7, 14, 30).
  - In-app notifications on/off.
  - Quiet hours (no notifications between X and Y time).
- Save preferences.

---

## 4. System Architecture

### 4.1 Architecture Overview

```
┌──────────────────────────────────────────────────────────────┐
│                        Frontend (UI Layer)                   │
│          React/JSP + Thymeleaf | HTML/CSS/JavaScript        │
├──────────────────────────────────────────────────────────────┤
│                    API Gateway / Security Layer              │
│                  (CORS, JWT Token Validation)                │
├──────────────────────────────────────────────────────────────┤
│                  Spring Boot REST API Layer                  │
│  ┌─────────────┬──────────────┬────────────────┬──────────┐ │
│  │ Controllers │ Services     │ Repositories   │ Entities │ │
│  │ (HTTP)      │ (Business    │ (Database)     │ (Models) │ │
│  │             │  Logic)      │                │          │ │
│  └─────────────┴──────────────┴────────────────┴──────────┘ │
├──────────────────────────────────────────────────────────────┤
│                      Data Access Layer (JPA/Hibernate)       │
├──────────────────────────────────────────────────────────────┤
│                      Database (MySQL/PostgreSQL)             │
└──────────────────────────────────────────────────────────────┘
                              │
            ┌─────────────────┴──────────────────┐
            │                                    │
     ┌──────────────────┐            ┌─────────────────────┐
     │ Spring Scheduler │            │ File Storage        │
     │ (Reminders)      │            │ (Local / AWS S3)    │
     └──────────────────┘            └─────────────────────┘
```

### 4.2 Backend Components

| Layer | Component | Responsibility |
|-------|-----------|-----------------|
| **Controller** | VehicleController, ServiceController, ServiceCenterController, ReminderController, AnalyticsController, UserController | HTTP request handling, request validation, response formatting |
| **Service** | VehicleService, ServiceRecordService, ServiceCenterService, ReminderService, AnalyticsService, FileService | Business logic, calculations, transactions, external integrations |
| **Repository** | VehicleRepository, ServiceRecordRepository, ServiceCenterRepository, ReminderRepository | Database CRUD operations (Spring Data JPA) |
| **Entity** | User, Vehicle, ServiceRecord, ServiceCenter, Review, Reminder | Database models (JPA entities) |
| **Config** | SecurityConfig, CorsConfig, SchedulerConfig | Spring configuration beans |
| **Exception** | VehicleNotFoundException, ServiceRecordNotFoundException, CustomExceptionHandler | Error handling and custom exceptions |
| **DTO** | VehicleDTO, ServiceRecordDTO, ServiceCenterDTO, ReviewDTO | Data transfer objects for API requests/responses |
| **Utility** | FileUtil, DateUtil, AnalyticsUtil | Helper functions |

---

## 5. Database Design

### 5.1 Entity Relationship Diagram (ERD)

```
┌─────────────┐
│   User      │
├─────────────┤
│ id (PK)     │
│ email       │
│ password    │
│ name        │
│ phone       │
│ address     │
│ created_at  │
└──────┬──────┘
       │
       │ 1:N
       │
┌──────▼──────────────┐
│   Vehicle           │
├─────────────────────┤
│ id (PK)             │
│ user_id (FK)        │
│ reg_number          │
│ make                │
│ model               │
│ year                │
│ engine_capacity     │
│ fuel_type           │
│ purchase_date       │
│ current_odometer    │
│ rc_path             │
│ created_at          │
└──────┬──────────────┘
       │
       │ 1:N
       │
┌──────▼────────────────────────┐
│   ServiceRecord                │
├────────────────────────────────┤
│ id (PK)                        │
│ vehicle_id (FK)                │
│ service_center_id (FK)         │
│ service_date                   │
│ odometer_reading               │
│ service_type (ENUM)            │
│ description                    │
│ cost                           │
│ next_service_date              │
│ next_service_odometer          │
│ receipt_path                   │
│ created_at                     │
└────────────────────────────────┘

┌─────────────────────────┐
│   ServiceCenter         │
├─────────────────────────┤
│ id (PK)                 │
│ user_id (FK)            │
│ name                    │
│ address                 │
│ phone                   │
│ email                   │
│ service_type (ENUM)     │
│ specialization (ENUM)   │
│ website                 │
│ latitude (optional)     │
│ longitude (optional)    │
│ created_at              │
└──────┬──────────────────┘
       │
       │ 1:N
       │
┌──────▼──────────────────┐
│   Review                │
├─────────────────────────┤
│ id (PK)                 │
│ service_center_id (FK)  │
│ user_id (FK)            │
│ rating                  │
│ comment                 │
│ recommend               │
│ created_at              │
└─────────────────────────┘

┌─────────────────────────┐
│   Reminder              │
├─────────────────────────┤
│ id (PK)                 │
│ vehicle_id (FK)         │
│ user_id (FK)            │
│ reminder_type (ENUM)    │
│ due_date                │
│ due_odometer            │
│ is_read                 │
│ dismissed_at            │
│ created_at              │
└─────────────────────────┘
```

### 5.2 Entity Specifications

#### User
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Vehicle
```sql
CREATE TABLE vehicle (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reg_number VARCHAR(20) UNIQUE NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    variant VARCHAR(50),
    year INT NOT NULL,
    engine_capacity INT,
    fuel_type ENUM('Petrol', 'Diesel', 'CNG', 'Electric') NOT NULL,
    purchase_date DATE,
    current_odometer INT DEFAULT 0,
    rc_path VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
```

#### ServiceRecord
```sql
CREATE TABLE service_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id BIGINT NOT NULL,
    service_center_id BIGINT,
    service_date DATE NOT NULL,
    odometer_reading INT NOT NULL,
    service_type ENUM('OIL_CHANGE', 'FILTER_REPLACEMENT', 'CHAIN_LUBE', 'TYRE_SERVICE', 'BATTERY_REPLACEMENT', 'BRAKE_SERVICE', 'SUSPENSION', 'GENERAL_SERVICE', 'OTHER') NOT NULL,
    description TEXT,
    cost DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20),  -- CASH, CARD, UPI, NET_BANKING
    service_center_name VARCHAR(100),
    next_service_date DATE,
    next_service_odometer INT,
    receipt_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE CASCADE
);
```

#### ServiceCenter
```sql
CREATE TABLE service_center (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    service_type VARCHAR(100),
    specialization VARCHAR(100),
    website VARCHAR(500),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
```

#### Review
```sql
CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_center_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(500),
    recommend BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_center (user_id, service_center_id),
    FOREIGN KEY (service_center_id) REFERENCES service_center(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
```

#### Reminder
```sql
CREATE TABLE reminder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reminder_type ENUM('DATE_BASED', 'KM_BASED') NOT NULL,
    due_date DATE,
    due_odometer INT,
    is_read BOOLEAN DEFAULT FALSE,
    dismissed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_vehicle_id (vehicle_id)
);
```

---

## 6. API Specifications

### 6.1 Base URL
```
http://localhost:8080/api/v1
```

### 6.2 Authentication
All endpoints (except `/auth/register` and `/auth/login`) require:
```
Authorization: Bearer <JWT_TOKEN>
```

### 6.3 Core Endpoints

#### **Authentication**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and get JWT token |
| POST | `/auth/logout` | Logout user |
| POST | `/auth/refresh-token` | Refresh JWT token |
| GET | `/auth/validate` | Validate token |

#### **User Profile**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/profile` | Get logged-in user profile |
| PUT | `/users/profile` | Update user profile |
| POST | `/users/change-password` | Change password |
| DELETE | `/users/account` | Delete user account |

#### **Vehicles**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/vehicles` | Create new vehicle |
| GET | `/vehicles` | Get all vehicles |
| GET | `/vehicles/{id}` | Get vehicle by ID |
| PUT | `/vehicles/{id}` | Update vehicle |
| DELETE | `/vehicles/{id}` | Delete vehicle |
| GET | `/vehicles/{id}/summary` | Get vehicle summary (costs, next service, etc.) |
| POST | `/vehicles/{id}/update-odometer` | Update current odometer |

#### **Service Records**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/services` | Create service record |
| GET | `/vehicles/{vehicleId}/services` | Get all services for vehicle |
| GET | `/services/{id}` | Get service by ID |
| PUT | `/services/{id}` | Update service record |
| DELETE | `/services/{id}` | Delete service record |
| POST | `/services/{id}/upload-receipt` | Upload receipt for service |
| GET | `/services/{id}/download-receipt` | Download receipt |

#### **Service Centers**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/service-centers` | Create service center |
| GET | `/service-centers` | Get all service centers |
| GET | `/service-centers/{id}` | Get service center by ID |
| PUT | `/service-centers/{id}` | Update service center |
| DELETE | `/service-centers/{id}` | Delete service center |
| GET | `/service-centers/{id}/reviews` | Get reviews for service center |
| POST | `/service-centers/{id}/reviews` | Add review |
| PUT | `/reviews/{reviewId}` | Update review |
| DELETE | `/reviews/{reviewId}` | Delete review |

#### **Reminders**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reminders` | Get all reminders for user |
| GET | `/reminders/upcoming` | Get upcoming reminders (next 7 days) |
| GET | `/vehicles/{vehicleId}/reminders` | Get reminders for vehicle |
| PUT | `/reminders/{id}/mark-read` | Mark reminder as read |
| PUT | `/reminders/{id}/dismiss` | Dismiss reminder |
| DELETE | `/reminders/{id}` | Delete reminder |

#### **Analytics**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/analytics/dashboard` | Get dashboard data |
| GET | `/analytics/monthly-costs` | Get monthly cost data |
| GET | `/analytics/cost-by-category` | Get cost breakdown by category |
| GET | `/analytics/vehicle-costs` | Get costs per vehicle |
| GET | `/analytics/report` | Generate custom report (with filters) |

### 6.4 Request/Response Examples

#### Create Vehicle
```http
POST /api/v1/vehicles
Authorization: Bearer <token>
Content-Type: application/json

{
  "regNumber": "KL01AB1234",
  "make": "Hero",
  "model": "MotoCorp Splendor",
  "variant": "Plus",
  "year": 2022,
  "engineCapacity": 100,
  "fuelType": "Petrol",
  "purchaseDate": "2022-06-15",
  "currentOdometer": 5000,
  "notes": "Regular commute bike"
}

Response: 201 Created
{
  "id": 1,
  "regNumber": "KL01AB1234",
  "make": "Hero",
  "model": "MotoCorp Splendor",
  "createdAt": "2026-01-10T12:30:00Z"
}
```

#### Log Service
```http
POST /api/v1/services
Authorization: Bearer <token>
Content-Type: application/json

{
  "vehicleId": 1,
  "serviceDate": "2026-01-10",
  "odometerReading": 6500,
  "serviceType": "Oil Change",
  "description": "Changed engine oil with Mobil Fully Synthetic 10W-40",
  "cost": 800.00,
  "serviceCenterId": 1,
  "nextServiceDate": "2026-04-10",
  "nextServiceOdometer": 9500
}

Response: 201 Created
{
  "id": 1,
  "vehicleId": 1,
  "serviceDate": "2026-01-10",
  "cost": 800.00,
  "createdAt": "2026-01-10T15:45:00Z"
}
```

#### Get Dashboard Analytics
```http
GET /api/v1/analytics/dashboard
Authorization: Bearer <token>

Response: 200 OK
{
  "totalVehicles": 3,
  "totalMaintenanceCost": 25000,
  "upcomingServicesCount": 5,
  "monthlyCosts": [
    { "month": "2025-12", "cost": 2500 },
    { "month": "2025-11", "cost": 3200 }
  ],
  "costByCategory": {
    "Oil Change": 5000,
    "Tyre Service": 8000,
    "General Service": 12000
  },
  "topExpensiveVehicles": [
    { "registration": "KL01AB1234", "totalCost": 12000 },
    { "registration": "KL02XY5678", "totalCost": 8500 }
  ]
}
```

---

## 7. Frontend Structure & Pages

### 7.1 Page Hierarchy

```
/
├── /login                    # Login page
├── /register                 # Registration page
├── /dashboard                # Main dashboard
├── /vehicles
│   ├── /vehicles             # Vehicle list
│   ├── /vehicles/add         # Add vehicle form
│   ├── /vehicles/:id         # Vehicle detail & services history
│   ├── /vehicles/:id/edit    # Edit vehicle
│   └── /services/:id/edit    # Edit service record
├── /service-centers
│   ├── /service-centers      # Service center list
│   ├── /service-centers/add  # Add service center
│   ├── /service-centers/:id  # Service center detail & reviews
│   └── /service-centers/:id/edit
├── /reminders                # Reminders list
├── /analytics
│   ├── /analytics            # Main analytics page
│   ├── /analytics/monthly    # Monthly cost report
│   ├── /analytics/vehicle    # Vehicle-wise analysis
│   └── /analytics/detailed   # Detailed report with export
├── /settings
│   ├── /settings/profile     # Edit profile
│   ├── /settings/notifications # Notification preferences
│   └── /settings/security    # Change password
└── /logout                   # Logout
```

### 7.2 Key UI Components

1. **Navigation Bar:**
   - Logo, user dropdown, notification bell (unread count), logout.

2. **Vehicle Card:**
   - Compact display: registration, make, model, year, last service date, next due date.

3. **Service Record Row:**
   - Date, type, cost, service center, actions (edit, delete, view receipt).

4. **Chart Components:**
   - Line chart (trends), Pie chart (categories), Bar chart (vehicles), Table (detailed data).

5. **Modal Dialogs:**
   - Confirm delete, view details, upload receipt.

6. **Forms:**
   - Add/edit vehicle, service, service center, review.
   - Client-side validation + server-side validation.

---

## 8. Non-Functional Requirements

### 8.1 Performance

| Metric | Target |
|--------|--------|
| Page load time | < 2 seconds |
| API response time | < 500 ms (avg), < 1 s (p95) |
| Database query time | < 100 ms (avg) |
| Concurrent users | 100+ |
| File upload max size | 5 MB |

### 8.2 Security

- HTTPS only (TLS 1.2+).
- JWT token with 15-minute expiry; refresh token with 7-day expiry.
- Password hashing: BCrypt with salt.
- Input validation and sanitization on all endpoints.
- SQL injection prevention via parameterized queries (JPA).
- CORS configured: allow only frontend domain(s).
- Rate limiting: max 100 requests per minute per IP.
- OWASP Top 10 compliance checks.

### 8.3 Scalability

- Database indexing on frequently queried columns (user_id, vehicle_id, service_date).
- Query optimization and pagination for large lists.
- Optional: caching layer (Redis) for user profile and service centers.
- Optional: horizontal scaling (multiple app instances behind load balancer).

### 8.4 Availability

- Target uptime: 99.5%.
- Graceful error handling with meaningful error messages.
- Logging: all requests, errors, and important events.
- Monitoring: real-time alerts for high error rates or slow responses.

### 8.5 Usability

- Responsive design: mobile, tablet, desktop.
- Accessibility: WCAG 2.1 Level AA compliance (alt text, keyboard navigation, color contrast).
- Intuitive UI: max 3 clicks to reach any feature.
- Error messages: clear, actionable, user-friendly.
- Help/tooltips: on complex forms.

### 8.6 Maintainability

- Code: clean, well-documented, follows Java/Spring conventions.
- Tests: unit tests (80%+ coverage), integration tests.
- Documentation: API docs (Swagger), architecture, deployment guide.
- Version control: Git with clear commit messages and branching strategy.

---

## 9. Development Roadmap (1 Week)

### Day 1: Setup & Database
- Create Spring Boot project with dependencies.
- Design and create database schema.
- Create JPA entities with annotations.
- Set up repository interfaces.

### Day 2: Backend - Auth & Vehicles
- Implement user registration/login with JWT.
- Implement vehicle CRUD endpoints and service layer.
- Add input validation and exception handling.
- Create vehicle-related tests.

### Day 3: Backend - Services & Service Centers
- Implement service record CRUD.
- Implement service center management.
- Add review functionality.
- Implement file upload for receipts.

### Day 4: Backend - Scheduler & Analytics
- Set up Spring Scheduler for reminders.
- Implement reminder generation logic.
- Create analytics endpoints (costs, trends, charts).
- Add email notification service.

### Day 5: Frontend Setup & Core Pages
- Set up React/JSP project.
- Create authentication pages (login, register).
- Create layout, navigation, dashboard skeleton.
- Integrate API calls for auth and vehicle list.

### Day 6: Frontend - Features
- Build vehicle management pages.
- Build service record pages.
- Build service center directory and reviews.
- Build reminders page.

### Day 7: Frontend - Analytics, Testing & Deployment
- Build analytics and dashboard with charts.
- Add charts using Chart.js or similar.
- End-to-end testing.
- Documentation.
- Deployment (local or cloud).

---

## 10. Success Criteria

### MVP (Minimum Viable Product)

✅ User registration and login  
✅ Add/view/edit/delete vehicles  
✅ Log service records with cost tracking  
✅ View maintenance history  
✅ Dashboard with upcoming services  
✅ Basic cost analytics (monthly, by category)  
✅ Service center management  
✅ Automated service reminders (scheduler)  
✅ Receipt upload for services  
✅ Clean, responsive UI  

### Nice-to-Have (Post-MVP)

🟡 Email notifications for reminders  
🟡 Service center ratings/reviews  
🟡 Mobile app (using same REST API)  
🟡 Advanced analytics (predictive maintenance)  
🟡 Map view of service centers (Google Maps integration)  
🟡 Multi-language support  
🟡 Offline capability (PWA)  

---

## 11. Testing Strategy

### Unit Tests
- Service layer methods (calculation logic, business rules).
- Repository queries.
- Utility functions.
- Target: 80% code coverage.

### Integration Tests
- API endpoints (happy path and error cases).
- Database transactions.
- File upload/download.

### Manual Testing
- UI responsiveness on different devices.
- User workflows (end-to-end scenarios).
- Edge cases (invalid data, large data sets).

### Test Tools
- JUnit 5 for unit tests.
- Mockito for mocking dependencies.
- Spring Boot Test for integration tests.
- Postman for API testing.
- Selenium for UI testing (optional).

---

## 12. Deployment

### Local Development
```bash
# Backend
cd backend
mvn clean install
mvn spring-boot:run

# Frontend (React)
cd frontend
npm install
npm start
```

### Production Deployment
- Backend: Package as JAR, deploy to AWS EC2 / Heroku / DigitalOcean.
- Frontend: Build React app, deploy to AWS S3 + CloudFront / Vercel / Netlify.
- Database: AWS RDS for managed MySQL.
- File storage: Local filesystem or AWS S3.
- Monitoring: AWS CloudWatch or external monitoring tool.

---

## 13. Glossary

| Term | Definition |
|------|-----------|
| **Odometer** | Instrument measuring vehicle miles/kilometers traveled. |
| **Service Due** | Next scheduled maintenance based on time or distance. |
| **Reminder** | Alert to user about upcoming service. |
| **Receipt** | Proof of service (bill, invoice, photos). |
| **Service Type** | Category of maintenance (oil change, tyre, etc.). |
| **Service Center** | Repair shop or authorized dealer. |
| **DTO** | Data Transfer Object; JSON payload for API. |
| **JWT** | JSON Web Token; stateless authentication mechanism. |
| **CORS** | Cross-Origin Resource Sharing; policy for API requests. |

---

## 14. Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-01-10 | Dev Team | Initial PRD |

---

**Document Status:** APPROVED  
**Next Review Date:** Post-MVP Launch  
**Contact:** development-team@vehiclemaintenance.app
