# Vehicle Service Management and Maintenance Analysis System

> **Scenario 16** | **Target Register Numbers:** 16, 46, 76, 106  
> Developed using **Java**, **DBMS (MySQL)**, **Software Engineering (SE)**, and **Python (Foundations of Data Science - FDS)**.

---

## 1. Project Overview & Problem Statement

An automobile service center manages customer information, vehicle details, service appointments, maintenance records, and billing information. Maintaining these records manually leads to scheduling conflicts, difficulties in tracking vehicle service history, and delays in identifying frequently serviced vehicles and common maintenance requirements.

This **Vehicle Service Management and Maintenance Analysis System** provides an end-to-end computerized solution to:
1. Maintain customer records (Name, Phone, Email, Address).
2. Maintain vehicle profiles (Registration number, Brand, Model, Vehicle Type, Manufacture Year).
3. Schedule service appointments and monitor status (`BOOKED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
4. Record maintenance operations with itemized labor charges and spare parts costs (`total_cost = labor_cost + parts_cost`).
5. Generate invoices/bills with payment modes (`CASH`, `UPI`, `CARD`, `NET_BANKING`) and payment status tracking (`PAID`, `PENDING`).
6. Generate analytical reports on vehicle service history, vehicle-wise maintenance expenditures, and monthly workshop workload.
7. Perform data extraction, preprocessing, and exploratory data analysis (EDA) with 8 publication-quality visualizations using Python.

---

## 2. Directory Structure

```
Vehicle Management System/
│
├── run_all.bat                              # Master launcher for all 4 modules
├── README.md                                # Project documentation
│
├── Java/                                    # Java Menu-Driven Console Application
│   ├── VehicleServiceManagement.java        # Single-file terminal menu application
│   ├── VehicleServiceManagement.class       # Compiled Java bytecode
│   ├── run_java_app.bat                     # Double-click runner for Java terminal app
│   └── download_mysql_driver.bat            # Helper to download MySQL JDBC Connector JAR
│
├── DBMS/                                    # Database Schema, Data, Queries & ER Diagram
│   ├── schema.sql                           # DDL script creating VehicleServiceDB & 5 tables
│   ├── sample_data.sql                      # Realistic sample dataset
│   ├── queries.sql                          # 20+ categorized SQL queries
│   ├── er_diagram.png                       # High-resolution ER diagram image
│   ├── er_diagram.md                        # ER diagram documentation with Mermaid schema
│   └── view_er_diagram.html                 # Browser viewer for ER Diagram
│
├── SE/                                      # Software Engineering Design Diagrams
│   ├── use_case_diagram.png                 # Use Case diagram image
│   ├── use_case_diagram.md                  # Use Case diagram Mermaid code & description
│   ├── uml_class_diagram.png                # UML Class diagram image
│   ├── uml_class_diagram.md                 # UML Class diagram Mermaid code & description
│   ├── system_architecture_diagram.png      # 5-Layer System Architecture diagram image
│   ├── system_architecture_diagram.md       # Architecture diagram Mermaid code
│   ├── dfd_level0_diagram.png               # Context Level DFD (Level 0) image
│   ├── dfd_level0_diagram.md                # DFD Level 0 documentation
│   ├── dfd_level1_diagram.png               # Decomposed DFD Level 1 image
│   ├── dfd_level1_diagram.md                # DFD Level 1 documentation
│   ├── activity_diagram.png                 # Service workflow Activity diagram image
│   ├── activity_diagram.md                  # Activity diagram documentation
│   ├── sequence_diagram.png                 # Service & Billing Sequence diagram image
│   ├── sequence_diagram.md                  # Sequence diagram documentation
│   ├── view_diagrams.html                   # Tabbed browser viewer for all 7 SE diagrams
│   └── view_diagrams.bat                    # One-click launcher for SE browser viewer
│
└── FDS/                                     # Foundations of Data Science (Python)
    ├── requirements.txt                     # Python dependencies (pandas, matplotlib, seaborn)
    ├── sample_data_standalone.py            # Standalone analysis script (runs without MySQL)
    ├── vehicle_service_analysis.py          # Live MySQL analysis script
    ├── run_visualizations.bat               # Launcher to generate and view all charts
    └── output/                              # 8 Generated Visualization PNGs:
        ├── 01_maintenance_cost_by_vehicle.png
        ├── 02_services_by_month.png
        ├── 03_service_type_distribution.png
        ├── 04_monthly_revenue_trend.png
        ├── 05_top5_most_serviced_vehicles.png
        ├── 06_cost_correlation_heatmap.png
        ├── 07_service_cost_distribution.png
        └── 08_labor_vs_parts_by_vehicle.png
```

---

## 3. Module Details & Instructions

### ☕ A. Java — Terminal Menu-Driven Application

The Java program (`VehicleServiceManagement.java`) features an interactive console menu with auto-calculation and formatted ASCII table output.

#### Main Menu Layout:
```
========================================
 VEHICLE SERVICE MANAGEMENT SYSTEM
 Mode: [Interactive In-Memory] / [MySQL Database]
========================================
 1. Customer Management
 2. Vehicle Management
 3. Appointment Management
 4. Service Record Management
 5. Billing Management
 6. Reports
 0. Exit
========================================
 Enter your choice:
```

#### Dual Execution Modes:
- **Standalone / In-Memory Mode (Automatic Fallback)**: If MySQL or the MySQL JDBC driver JAR is not installed, the application automatically launches in **Interactive In-Memory Mode** preloaded with realistic customer, vehicle, appointment, and service records. All features (Add, Search, View, Cost Calculation, and Reports) work 100% interactively without errors.
- **MySQL Database Mode**: When `mysql-connector-j-8.3.0.jar` is placed in the `Java/` folder and MySQL is running, it automatically switches to live MySQL JDBC persistence.

#### How to Run:
- **Option 1**: Double-click `Java/run_java_app.bat`.
- **Option 2 (Terminal)**:
  ```powershell
  cd "D:\Downloads\Vehicle Management System\Java"
  javac VehicleServiceManagement.java
  java -cp . VehicleServiceManagement
  ```

---

### 🗄️ B. DBMS — Database Design & Queries

The database layer consists of 5 normalized relational tables:
1. `Customer`: Customer details (`customer_id` PK, name, phone, email, address, created_at).
2. `Vehicle`: Vehicle records (`vehicle_id` PK, `customer_id` FK, vehicle_number UNIQUE, brand, model, vehicle_type, manufacture_year).
3. `Appointment`: Scheduling (`appointment_id` PK, `customer_id` FK, `vehicle_id` FK, appointment_date, issue_description, status).
4. `Service_Record`: Maintenance details (`service_id` PK, `appointment_id` FK, `vehicle_id` FK, service_date, service_description, labor_cost, parts_cost, total_cost, next_service_date).
5. `Bill`: Billing & invoices (`bill_id` PK, `service_id` FK, bill_date, amount, payment_status, payment_method).

#### Files:
- **`DBMS/schema.sql`**: Complete DDL with primary and foreign key constraints.
- **`DBMS/sample_data.sql`**: Populates the database with realistic Indian automobile records.
- **`DBMS/queries.sql`**: 20+ comprehensive queries covering CRUD, Inner/Left Joins, Aggregates, Group By, Having, and Subqueries.
- **`DBMS/er_diagram.png`**: High-resolution image showing table structures, PK/FK, and 1:N cardinalities.
- **`DBMS/view_er_diagram.html`**: Browser viewer for the ER diagram.

---

### 📐 C. SE — Software Engineering Diagrams

All required diagrams are provided in dual formats:
- **High-Resolution `.png` Images** (openable directly in any image viewer).
- **Markdown `.md` Files** (containing Mermaid diagram source code and documentation).

| Diagram | Image File | Markdown Source |
|---|---|---|
| **Use Case Diagram** | `SE/use_case_diagram.png` | `SE/use_case_diagram.md` |
| **UML Class Diagram** | `SE/uml_class_diagram.png` | `SE/uml_class_diagram.md` |
| **System Architecture Diagram** | `SE/system_architecture_diagram.png` | `SE/system_architecture_diagram.md` |
| **DFD Level 0 (Context)** | `SE/dfd_level0_diagram.png` | `SE/dfd_level0_diagram.md` |
| **DFD Level 1 Diagram** | `SE/dfd_level1_diagram.png` | `SE/dfd_level1_diagram.md` |
| **Activity Diagram** | `SE/activity_diagram.png` | `SE/activity_diagram.md` |
| **Sequence Diagram** | `SE/sequence_diagram.png` | `SE/sequence_diagram.md` |

#### How to View:
- **Option 1**: Double-click any `.png` file to open directly in Windows Photos.
- **Option 2**: Double-click `SE/view_diagrams.bat` to open an interactive tabbed viewer in your default browser.

---

### 📊 D. FDS — Foundations of Data Science (Visualizations)

The Python FDS module handles data preprocessing, summary statistics, and visual analytics.

#### Generated Visualizations (`FDS/output/`):
1. **01_maintenance_cost_by_vehicle.png**: Horizontal bar chart comparing cumulative maintenance expenditure per vehicle.
2. **02_services_by_month.png**: Bar chart tracking service volume over months.
3. **03_service_type_distribution.png**: Donut / Pie chart displaying proportions of service types.
4. **04_monthly_revenue_trend.png**: Line plot showing revenue trends.
5. **05_top5_most_serviced_vehicles.png**: Bar chart of the top 5 most frequently serviced vehicles.
6. **06_cost_correlation_heatmap.png**: Correlation heatmap between labor cost, parts cost, and total cost.
7. **07_service_cost_distribution.png**: Box plot analyzing cost spreads, medians, and quartiles.
8. **08_labor_vs_parts_by_vehicle.png**: Stacked bar chart comparing labor vs. parts breakdown per vehicle.

#### How to Run:
- **Option 1**: Double-click `FDS/run_visualizations.bat`.
- **Option 2 (Terminal)**:
  ```powershell
  cd "D:\Downloads\Vehicle Management System\FDS"
  python sample_data_standalone.py
  ```

---

## 4. Master Quick Launcher

A single batch file **`run_all.bat`** is provided in the root directory:

```
============================================================
  VEHICLE SERVICE MANAGEMENT SYSTEM - MASTER RUNNER
============================================================

  [1] Launch Java Menu-Driven Console Application
  [2] Run FDS Data Science Visualizations (Python)
  [3] Open FDS Visualizations Output Folder (8 PNG Charts)
  [4] Open DBMS ER Diagram Image (er_diagram.png)
  [5] Open DBMS Folder (SQL Queries & Schema)
  [6] Open Software Engineering (SE) Diagrams in Browser
  [7] Open SE Diagrams Folder (7 PNG Diagrams)
  [0] Exit
============================================================
```

Double-click **`run_all.bat`** to access any module instantly.
