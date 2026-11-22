# ER Diagram – Vehicle Service Management and Maintenance Analysis System

![ER Diagram](er_diagram.png)

This Entity-Relationship diagram illustrates the database schema for the Vehicle Service Management and Maintenance Analysis System. It covers five core entities and their relationships.

```mermaid
erDiagram
    Customer {
        INT customer_id PK "AUTO_INCREMENT"
        VARCHAR customer_name "NOT NULL"
        VARCHAR phone "NOT NULL"
        VARCHAR email
        VARCHAR address
        TIMESTAMP created_at "DEFAULT CURRENT_TIMESTAMP"
    }

    Vehicle {
        INT vehicle_id PK "AUTO_INCREMENT"
        INT customer_id FK "NOT NULL"
        VARCHAR vehicle_number "NOT NULL, UNIQUE"
        VARCHAR brand
        VARCHAR model "NOT NULL"
        VARCHAR vehicle_type
        INT manufacture_year
    }

    Appointment {
        INT appointment_id PK "AUTO_INCREMENT"
        INT customer_id FK "NOT NULL"
        INT vehicle_id FK "NOT NULL"
        DATE appointment_date "NOT NULL"
        VARCHAR issue_description
        VARCHAR status "NOT NULL, DEFAULT BOOKED"
    }

    Service_Record {
        INT service_id PK "AUTO_INCREMENT"
        INT appointment_id FK
        INT vehicle_id FK "NOT NULL"
        DATE service_date "NOT NULL"
        VARCHAR service_description "NOT NULL"
        DECIMAL labor_cost "DEFAULT 0"
        DECIMAL parts_cost "DEFAULT 0"
        DECIMAL total_cost "DEFAULT 0"
        DATE next_service_date
    }

    Bill {
        INT bill_id PK "AUTO_INCREMENT"
        INT service_id FK "NOT NULL"
        DATE bill_date "NOT NULL"
        DECIMAL amount "NOT NULL"
        VARCHAR payment_status "DEFAULT PENDING"
        VARCHAR payment_method
    }

    Customer ||--o{ Vehicle : "owns"
    Customer ||--o{ Appointment : "books"
    Vehicle  ||--o{ Appointment : "scheduled for"
    Vehicle  ||--o{ Service_Record : "serviced in"
    Appointment ||--o{ Service_Record : "results in"
    Service_Record ||--o{ Bill : "billed as"
```

## Relationships

| Relationship | Type | Description |
|---|---|---|
| Customer → Vehicle | 1 : N | A customer can own multiple vehicles |
| Customer → Appointment | 1 : N | A customer can book multiple appointments |
| Vehicle → Appointment | 1 : N | A vehicle can have multiple appointments |
| Vehicle → Service_Record | 1 : N | A vehicle can have multiple service records |
| Appointment → Service_Record | 1 : N | An appointment can result in multiple service records |
| Service_Record → Bill | 1 : N | A service record can have one or more bills |
