-- ============================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS VehicleServiceDB;
USE VehicleServiceDB;

-- ============================================================
-- 1. Customer Table
-- ============================================================
CREATE TABLE IF NOT EXISTS Customer (
    customer_id   INT            PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100)   NOT NULL,
    phone         VARCHAR(15)    NOT NULL,
    email         VARCHAR(120),
    address       VARCHAR(255),
    created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 2. Vehicle Table
-- ============================================================
CREATE TABLE IF NOT EXISTS Vehicle (
    vehicle_id       INT            PRIMARY KEY AUTO_INCREMENT,
    customer_id      INT            NOT NULL,
    vehicle_number   VARCHAR(20)    NOT NULL UNIQUE,
    brand            VARCHAR(60),
    model            VARCHAR(60)    NOT NULL,
    vehicle_type     VARCHAR(40),
    manufacture_year INT,
    CONSTRAINT fk_vehicle_customer
        FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================================================
-- 3. Appointment Table
-- ============================================================
CREATE TABLE IF NOT EXISTS Appointment (
    appointment_id   INT            PRIMARY KEY AUTO_INCREMENT,
    customer_id      INT            NOT NULL,
    vehicle_id       INT            NOT NULL,
    appointment_date DATE           NOT NULL,
    issue_description VARCHAR(500),
    status           VARCHAR(30)    NOT NULL DEFAULT 'BOOKED',
    CONSTRAINT fk_appointment_customer
        FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_appointment_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================================================
-- 4. Service_Record Table
-- ============================================================
CREATE TABLE IF NOT EXISTS Service_Record (
    service_id          INT            PRIMARY KEY AUTO_INCREMENT,
    appointment_id      INT,
    vehicle_id          INT            NOT NULL,
    service_date        DATE           NOT NULL,
    service_description VARCHAR(500)   NOT NULL,
    labor_cost          DECIMAL(10,2)  DEFAULT 0,
    parts_cost          DECIMAL(10,2)  DEFAULT 0,
    total_cost          DECIMAL(10,2)  DEFAULT 0,
    next_service_date   DATE,
    CONSTRAINT fk_service_appointment
        FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_service_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================================================
-- 5. Bill Table
-- ============================================================
CREATE TABLE IF NOT EXISTS Bill (
    bill_id        INT            PRIMARY KEY AUTO_INCREMENT,
    service_id     INT            NOT NULL,
    bill_date      DATE           NOT NULL,
    amount         DECIMAL(10,2)  NOT NULL,
    payment_status VARCHAR(30)    DEFAULT 'PENDING',
    payment_method VARCHAR(30),
    CONSTRAINT fk_bill_service
        FOREIGN KEY (service_id) REFERENCES Service_Record(service_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================================================
-- End of Schema
-- ============================================================
