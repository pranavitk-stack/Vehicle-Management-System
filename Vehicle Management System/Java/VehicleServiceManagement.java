// Vehicle Service Management and Maintenance Analysis System

import java.sql.*;
import java.util.*;

public class VehicleServiceManagement {

    // Database connection parameters
    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "3306";
    private static final String DB_NAME     = "VehicleServiceDB";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_URL      = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                                            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Mode flag
    private static boolean isMysqlMode = false;

    // In-memory data store for standalone/demo execution without MySQL driver
    static class CustData {
        int id; String name, phone, email, address, createdAt;
        CustData(int id, String name, String phone, String email, String address, String createdAt) {
            this.id = id; this.name = name; this.phone = phone; this.email = email; this.address = address; this.createdAt = createdAt;
        }
    }
    static class VehData {
        int id, custId; String regNo, brand, model, type; int year;
        VehData(int id, int custId, String regNo, String brand, String model, String type, int year) {
            this.id = id; this.custId = custId; this.regNo = regNo; this.brand = brand; this.model = model; this.type = type; this.year = year;
        }
    }
    static class ApptData {
        int id, custId, vehId; String date, issue, status;
        ApptData(int id, int custId, int vehId, String date, String issue, String status) {
            this.id = id; this.custId = custId; this.vehId = vehId; this.date = date; this.issue = issue; this.status = status;
        }
    }
    static class SrvData {
        int id, apptId, vehId; String date, description; double labor, parts, total; String nextDate;
        SrvData(int id, int apptId, int vehId, String date, String description, double labor, double parts, double total, String nextDate) {
            this.id = id; this.apptId = apptId; this.vehId = vehId; this.date = date; this.description = description;
            this.labor = labor; this.parts = parts; this.total = total; this.nextDate = nextDate;
        }
    }
    static class BillData {
        int id, srvId; String date; double amount; String status, method;
        BillData(int id, int srvId, String date, double amount, String status, String method) {
            this.id = id; this.srvId = srvId; this.date = date; this.amount = amount; this.status = status; this.method = method;
        }
    }

    private static final List<CustData> memCust = new ArrayList<>();
    private static final List<VehData> memVeh = new ArrayList<>();
    private static final List<ApptData> memAppt = new ArrayList<>();
    private static final List<SrvData> memSrv = new ArrayList<>();
    private static final List<BillData> memBill = new ArrayList<>();

    private static int nextCustId = 6, nextVehId = 7, nextApptId = 7, nextSrvId = 7, nextBillId = 7;

    private static final Scanner scanner = new Scanner(System.in);

    // ================================================================
    //                     DATABASE / INIT HELPERS
    // ================================================================
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void initInMemoryData() {
        memCust.clear(); memVeh.clear(); memAppt.clear(); memSrv.clear(); memBill.clear();

        memCust.add(new CustData(1, "Rajesh Kumar", "9876543210", "rajesh@email.com", "12 Anna Nagar, Chennai", "2024-01-10 10:00:00"));
        memCust.add(new CustData(2, "Priya Sharma", "9123456780", "priya@email.com", "45 MG Road, Coimbatore", "2024-02-15 11:30:00"));
        memCust.add(new CustData(3, "Arun Venkatesh", "9988776655", "arun@email.com", "78 Kamaraj Salai, Madurai", "2024-03-05 09:15:00"));
        memCust.add(new CustData(4, "Deepa Murugan", "9871234560", "deepa@email.com", "23 Nehru St, Trichy", "2024-03-20 14:00:00"));
        memCust.add(new CustData(5, "Karthik Rajan", "9900112233", "karthik@email.com", "90 Beach Road, Pondicherry", "2024-04-01 16:45:00"));

        memVeh.add(new VehData(1, 1, "TN 01 AB 1234", "Honda", "City", "Four Wheeler", 2021));
        memVeh.add(new VehData(2, 1, "TN 01 CD 5678", "Yamaha", "FZ-S", "Two Wheeler", 2022));
        memVeh.add(new VehData(3, 2, "TN 38 XY 9012", "Hyundai", "Creta", "Four Wheeler", 2020));
        memVeh.add(new VehData(4, 3, "TN 58 KL 3456", "Maruti Suzuki", "Swift", "Four Wheeler", 2019));
        memVeh.add(new VehData(5, 4, "TN 45 PQ 7890", "Royal Enfield", "Classic 350", "Two Wheeler", 2023));
        memVeh.add(new VehData(6, 5, "TN 32 MN 4567", "Tata", "Nexon", "Four Wheeler", 2022));

        memAppt.add(new ApptData(1, 1, 1, "2024-01-15", "Periodic 20,000 km general service", "COMPLETED"));
        memAppt.add(new ApptData(2, 1, 2, "2024-02-10", "Chain lubrication and oil change", "COMPLETED"));
        memAppt.add(new ApptData(3, 2, 3, "2024-02-20", "Brake pads replacement and AC check", "COMPLETED"));
        memAppt.add(new ApptData(4, 3, 4, "2024-03-12", "Full suspension overhaul & wheel alignment", "COMPLETED"));
        memAppt.add(new ApptData(5, 4, 5, "2024-03-25", "First free service + general inspection", "COMPLETED"));
        memAppt.add(new ApptData(6, 5, 6, "2024-04-05", "Battery check, brake fluid top-up", "BOOKED"));

        memSrv.add(new SrvData(1, 1, 1, "2024-01-15", "Engine oil change, oil filter, wash", 1200.0, 2300.0, 3500.0, "2024-07-15"));
        memSrv.add(new SrvData(2, 2, 2, "2024-02-10", "Motul chain lube, engine oil, plug clean", 500.0, 900.0, 1400.0, "2024-08-10"));
        memSrv.add(new SrvData(3, 3, 3, "2024-02-20", "Front & rear brake pads, AC gas refill", 2000.0, 4800.0, 6800.0, "2024-08-20"));
        memSrv.add(new SrvData(4, 4, 4, "2024-03-12", "Front shock absorber replacement", 2500.0, 6000.0, 8500.0, "2024-09-12"));
        memSrv.add(new SrvData(5, 5, 5, "2024-03-25", "Engine oil, chain adjustment, wash", 600.0, 1200.0, 1800.0, "2024-09-25"));
        memSrv.add(new SrvData(6, 1, 1, "2024-04-10", "Wheel alignment, balancing, tyre rotation", 1000.0, 1200.0, 2200.0, "2024-10-10"));

        memBill.add(new BillData(1, 1, "2024-01-15", 3500.0, "PAID", "UPI"));
        memBill.add(new BillData(2, 2, "2024-02-10", 1400.0, "PAID", "CASH"));
        memBill.add(new BillData(3, 3, "2024-02-20", 6800.0, "PAID", "CARD"));
        memBill.add(new BillData(4, 4, "2024-03-12", 8500.0, "PENDING", "NET_BANKING"));
        memBill.add(new BillData(5, 5, "2024-03-25", 1800.0, "PAID", "UPI"));
        memBill.add(new BillData(6, 6, "2024-04-10", 2200.0, "PAID", "CASH"));
    }

    // ================================================================
    //                     TABLE FORMATTING HELPERS
    // ================================================================
    private static void printLine(int width) {
        StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < width - 2; i++) sb.append("-");
        sb.append("+");
        System.out.println(sb.toString());
    }

    private static void printRow(String format, Object... args) {
        System.out.printf("| " + format + " |%n", args);
    }

    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static String truncate(String text, int max) {
        if (text == null) return "N/A";
        return text.length() > max ? text.substring(0, max - 3) + "..." : text;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  ** Invalid number. Please enter an integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("  ** Invalid amount. Please enter a valid decimal number.");
            }
        }
    }

    private static String readString(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("  ** Input cannot be empty. Please try again.");
            }
        } while (value.isEmpty());
        return value;
    }

    // ================================================================
    //                         MAIN METHOD
    // ================================================================
    public static void main(String[] args) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("   VEHICLE SERVICE MANAGEMENT AND MAINTENANCE ANALYSIS");
        System.out.println("============================================================");

        // Check for MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = getConnection()) {
                isMysqlMode = true;
                System.out.println(" [STATUS] MySQL Connected successfully to: " + DB_NAME);
            } catch (SQLException e) {
                isMysqlMode = false;
                System.out.println(" [NOTICE] MySQL server unreachable (" + e.getMessage() + ")");
                System.out.println("          Switching to Interactive In-Memory Mode with pre-loaded data.");
            }
        } catch (ClassNotFoundException e) {
            isMysqlMode = false;
            System.out.println(" [NOTICE] MySQL JDBC Driver not found on classpath.");
            System.out.println("          Switching to Interactive In-Memory Mode with pre-loaded data.");
            System.out.println("          (All menus, records, and reports are fully functional!)");
        }

        if (!isMysqlMode) {
            initInMemoryData();
        }

        // Main menu loop
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println(" VEHICLE SERVICE MANAGEMENT SYSTEM");
            System.out.println(" Mode: " + (isMysqlMode ? "[MySQL Database]" : "[Interactive In-Memory]"));
            System.out.println("========================================");
            System.out.println(" 1. Customer Management");
            System.out.println(" 2. Vehicle Management");
            System.out.println(" 3. Appointment Management");
            System.out.println(" 4. Service Record Management");
            System.out.println(" 5. Billing Management");
            System.out.println(" 6. Reports");
            System.out.println(" 0. Exit");
            System.out.println("========================================");

            int choice = readInt(" Enter your choice: ");

            switch (choice) {
                case 1: customerMenu();       break;
                case 2: vehicleMenu();        break;
                case 3: appointmentMenu();    break;
                case 4: serviceRecordMenu();  break;
                case 5: billingMenu();        break;
                case 6: reportsMenu();        break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using Vehicle Service Management System. Goodbye!");
                    break;
                default:
                    System.out.println("  ** Invalid choice. Please select 0-6.");
            }
        }
        scanner.close();
    }

    // ================================================================
    //                    1. CUSTOMER MANAGEMENT
    // ================================================================
    private static void customerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Customer Management ---");
            System.out.println(" 1. Add Customer");
            System.out.println(" 2. View All Customers");
            System.out.println(" 3. Search Customer by ID");
            System.out.println(" 0. Back to Main Menu");

            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: addCustomer();        break;
                case 2: viewAllCustomers();   break;
                case 3: searchCustomerById(); break;
                case 0: back = true;          break;
                default: System.out.println("  ** Invalid choice.");
            }
        }
    }

    private static void addCustomer() {
        System.out.println("\n--- Add New Customer ---");
        String name    = readString(" Customer Name : ");
        String phone   = readString(" Phone         : ");
        String email   = readString(" Email         : ");
        String address = readString(" Address       : ");

        if (isMysqlMode) {
            String sql = "INSERT INTO Customer (customer_name, phone, email, address) VALUES (?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name); ps.setString(2, phone); ps.setString(3, email); ps.setString(4, address);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) System.out.println(" >> Customer added successfully! ID: " + keys.getInt(1));
            } catch (SQLException e) {
                System.out.println(" >> Error adding customer: " + e.getMessage());
            }
        } else {
            int newId = nextCustId++;
            memCust.add(new CustData(newId, name, phone, email, address, "Just now"));
            System.out.println(" >> Customer added successfully! ID: " + newId);
        }
        pause();
    }

    private static void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        String fmt = "%-5s %-20s %-15s %-25s %-22s %-20s";
        int width = 117;
        printLine(width);
        printRow(fmt, "ID", "Name", "Phone", "Email", "Address", "Created At");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT customer_id, customer_name, phone, email, address, created_at FROM Customer ORDER BY customer_id";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    printRow(fmt, rs.getInt("customer_id"), truncate(rs.getString("customer_name"), 20),
                        truncate(rs.getString("phone"), 15), truncate(rs.getString("email"), 25),
                        truncate(rs.getString("address"), 22), rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : "N/A");
                }
                if (!found) printRow("%-113s", "No customers found.");
            } catch (SQLException e) {
                System.out.println(" >> Error retrieving customers: " + e.getMessage());
            }
        } else {
            for (CustData c : memCust) {
                printRow(fmt, c.id, truncate(c.name, 20), truncate(c.phone, 15), truncate(c.email, 25), truncate(c.address, 22), c.createdAt);
            }
        }
        printLine(width);
        pause();
    }

    private static void searchCustomerById() {
        System.out.println("\n--- Search Customer by ID ---");
        int id = readInt(" Enter Customer ID: ");
        boolean found = false;

        if (isMysqlMode) {
            String sql = "SELECT customer_id, customer_name, phone, email, address, created_at FROM Customer WHERE customer_id = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    found = true;
                    System.out.println("\n Customer Found:");
                    System.out.println("   ID         : " + rs.getInt("customer_id"));
                    System.out.println("   Name       : " + rs.getString("customer_name"));
                    System.out.println("   Phone      : " + rs.getString("phone"));
                    System.out.println("   Email      : " + rs.getString("email"));
                    System.out.println("   Address    : " + rs.getString("address"));
                    System.out.println("   Created At : " + rs.getTimestamp("created_at"));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (CustData c : memCust) {
                if (c.id == id) {
                    found = true;
                    System.out.println("\n Customer Found:");
                    System.out.println("   ID         : " + c.id);
                    System.out.println("   Name       : " + c.name);
                    System.out.println("   Phone      : " + c.phone);
                    System.out.println("   Email      : " + c.email);
                    System.out.println("   Address    : " + c.address);
                    System.out.println("   Created At : " + c.createdAt);
                    break;
                }
            }
        }
        if (!found) System.out.println(" >> Customer with ID " + id + " not found.");
        pause();
    }

    // ================================================================
    //                    2. VEHICLE MANAGEMENT
    // ================================================================
    private static void vehicleMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Vehicle Management ---");
            System.out.println(" 1. Add Vehicle");
            System.out.println(" 2. View All Vehicles");
            System.out.println(" 3. Search Vehicle by Reg No");
            System.out.println(" 0. Back to Main Menu");

            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: addVehicle();        break;
                case 2: viewAllVehicles();   break;
                case 3: searchVehicleById(); break;
                case 0: back = true;         break;
                default: System.out.println("  ** Invalid choice.");
            }
        }
    }

    private static void addVehicle() {
        System.out.println("\n--- Add New Vehicle ---");
        int custId   = readInt(" Customer ID       : ");
        String regNo = readString(" Reg Number (e.g. TN 01 AB 1234): ");
        String brand = readString(" Brand (e.g. Honda): ");
        String model = readString(" Model (e.g. City) : ");
        String type  = readString(" Type (Two/Four Wheeler): ");
        int year     = readInt(" Manufacture Year  : ");

        if (isMysqlMode) {
            String sql = "INSERT INTO Vehicle (customer_id, vehicle_number, brand, model, vehicle_type, manufacture_year) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, custId); ps.setString(2, regNo); ps.setString(3, brand); ps.setString(4, model); ps.setString(5, type); ps.setInt(6, year);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) System.out.println(" >> Vehicle registered successfully! ID: " + keys.getInt(1));
            } catch (SQLException e) {
                System.out.println(" >> Error registering vehicle: " + e.getMessage());
            }
        } else {
            int newId = nextVehId++;
            memVeh.add(new VehData(newId, custId, regNo, brand, model, type, year));
            System.out.println(" >> Vehicle registered successfully! ID: " + newId);
        }
        pause();
    }

    private static void viewAllVehicles() {
        System.out.println("\n--- All Vehicles ---");
        String fmt = "%-5s %-16s %-15s %-15s %-15s %-6s %-18s";
        int width = 96;
        printLine(width);
        printRow(fmt, "ID", "Reg Number", "Brand", "Model", "Type", "Year", "Customer Name");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT v.vehicle_id, v.vehicle_number, v.brand, v.model, v.vehicle_type, v.manufacture_year, c.customer_name "
                       + "FROM Vehicle v JOIN Customer c ON v.customer_id = c.customer_id ORDER BY v.vehicle_id";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    printRow(fmt, rs.getInt("vehicle_id"), rs.getString("vehicle_number"), rs.getString("brand"),
                        rs.getString("model"), rs.getString("vehicle_type"), rs.getInt("manufacture_year"),
                        truncate(rs.getString("customer_name"), 18));
                }
                if (!found) printRow("%-92s", "No vehicles registered.");
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (VehData v : memVeh) {
                String cName = "Customer #" + v.custId;
                for (CustData c : memCust) if (c.id == v.custId) { cName = c.name; break; }
                printRow(fmt, v.id, v.regNo, v.brand, v.model, v.type, v.year, truncate(cName, 18));
            }
        }
        printLine(width);
        pause();
    }

    private static void searchVehicleById() {
        System.out.println("\n--- Search Vehicle by Reg Number ---");
        String reg = readString(" Enter Vehicle Reg Number: ");
        boolean found = false;

        if (isMysqlMode) {
            String sql = "SELECT v.*, c.customer_name, c.phone FROM Vehicle v JOIN Customer c ON v.customer_id = c.customer_id WHERE UPPER(v.vehicle_number) = UPPER(?)";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, reg);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    found = true;
                    System.out.println("\n Vehicle Found:");
                    System.out.println("   Vehicle ID    : " + rs.getInt("vehicle_id"));
                    System.out.println("   Reg Number    : " + rs.getString("vehicle_number"));
                    System.out.println("   Brand & Model : " + rs.getString("brand") + " " + rs.getString("model"));
                    System.out.println("   Type / Year   : " + rs.getString("vehicle_type") + " / " + rs.getInt("manufacture_year"));
                    System.out.println("   Owner         : " + rs.getString("customer_name") + " (Phone: " + rs.getString("phone") + ")");
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (VehData v : memVeh) {
                if (v.regNo.equalsIgnoreCase(reg)) {
                    found = true;
                    String cName = "Customer #" + v.custId, cPhone = "N/A";
                    for (CustData c : memCust) if (c.id == v.custId) { cName = c.name; cPhone = c.phone; break; }
                    System.out.println("\n Vehicle Found:");
                    System.out.println("   Vehicle ID    : " + v.id);
                    System.out.println("   Reg Number    : " + v.regNo);
                    System.out.println("   Brand & Model : " + v.brand + " " + v.model);
                    System.out.println("   Type / Year   : " + v.type + " / " + v.year);
                    System.out.println("   Owner         : " + cName + " (Phone: " + cPhone + ")");
                    break;
                }
            }
        }
        if (!found) System.out.println(" >> Vehicle not found for Reg No: " + reg);
        pause();
    }

    // ================================================================
    //                   3. APPOINTMENT MANAGEMENT
    // ================================================================
    private static void appointmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Appointment Management ---");
            System.out.println(" 1. Book Appointment");
            System.out.println(" 2. View All Appointments");
            System.out.println(" 3. Search Appointment by ID");
            System.out.println(" 0. Back to Main Menu");

            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: addAppointment();        break;
                case 2: viewAllAppointments();   break;
                case 3: searchAppointmentById(); break;
                case 0: back = true;             break;
                default: System.out.println("  ** Invalid choice.");
            }
        }
    }

    private static void addAppointment() {
        System.out.println("\n--- Book Service Appointment ---");
        int custId    = readInt(" Customer ID                 : ");
        int vehId     = readInt(" Vehicle ID                  : ");
        String date   = readString(" Appointment Date (YYYY-MM-DD): ");
        String issue  = readString(" Issue Description           : ");

        if (isMysqlMode) {
            String sql = "INSERT INTO Appointment (customer_id, vehicle_id, appointment_date, issue_description, status) VALUES (?, ?, ?, ?, 'BOOKED')";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, custId); ps.setInt(2, vehId); ps.setDate(3, java.sql.Date.valueOf(date)); ps.setString(4, issue);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) System.out.println(" >> Appointment booked! ID: " + keys.getInt(1));
            } catch (Exception e) {
                System.out.println(" >> Error booking appointment: " + e.getMessage());
            }
        } else {
            int newId = nextApptId++;
            memAppt.add(new ApptData(newId, custId, vehId, date, issue, "BOOKED"));
            System.out.println(" >> Appointment booked! ID: " + newId);
        }
        pause();
    }

    private static void viewAllAppointments() {
        System.out.println("\n--- All Appointments ---");
        String fmt = "%-5s %-12s %-15s %-18s %-30s %-12s";
        int width = 100;
        printLine(width);
        printRow(fmt, "ID", "Date", "Reg Number", "Customer Name", "Issue Description", "Status");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT a.appointment_id, a.appointment_date, v.vehicle_number, c.customer_name, a.issue_description, a.status "
                       + "FROM Appointment a JOIN Customer c ON a.customer_id = c.customer_id JOIN Vehicle v ON a.vehicle_id = v.vehicle_id ORDER BY a.appointment_id";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    printRow(fmt, rs.getInt("appointment_id"), rs.getDate("appointment_date"), rs.getString("vehicle_number"),
                        truncate(rs.getString("customer_name"), 18), truncate(rs.getString("issue_description"), 30), rs.getString("status"));
                }
                if (!found) printRow("%-96s", "No appointments found.");
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (ApptData a : memAppt) {
                String cName = "Cust #" + a.custId, regNo = "Veh #" + a.vehId;
                for (CustData c : memCust) if (c.id == a.custId) { cName = c.name; break; }
                for (VehData v : memVeh) if (v.id == a.vehId) { regNo = v.regNo; break; }
                printRow(fmt, a.id, a.date, regNo, truncate(cName, 18), truncate(a.issue, 30), a.status);
            }
        }
        printLine(width);
        pause();
    }

    private static void searchAppointmentById() {
        System.out.println("\n--- Search Appointment by ID ---");
        int id = readInt(" Enter Appointment ID: ");
        boolean found = false;

        if (isMysqlMode) {
            String sql = "SELECT a.*, c.customer_name, v.vehicle_number FROM Appointment a JOIN Customer c ON a.customer_id = c.customer_id JOIN Vehicle v ON a.vehicle_id = v.vehicle_id WHERE a.appointment_id = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    found = true;
                    System.out.println("\n Appointment Details:");
                    System.out.println("   ID       : " + rs.getInt("appointment_id"));
                    System.out.println("   Date     : " + rs.getDate("appointment_date"));
                    System.out.println("   Customer : " + rs.getString("customer_name"));
                    System.out.println("   Vehicle  : " + rs.getString("vehicle_number"));
                    System.out.println("   Issue    : " + rs.getString("issue_description"));
                    System.out.println("   Status   : " + rs.getString("status"));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (ApptData a : memAppt) {
                if (a.id == id) {
                    found = true;
                    String cName = "Cust #" + a.custId, regNo = "Veh #" + a.vehId;
                    for (CustData c : memCust) if (c.id == a.custId) { cName = c.name; break; }
                    for (VehData v : memVeh) if (v.id == a.vehId) { regNo = v.regNo; break; }
                    System.out.println("\n Appointment Details:");
                    System.out.println("   ID       : " + a.id);
                    System.out.println("   Date     : " + a.date);
                    System.out.println("   Customer : " + cName);
                    System.out.println("   Vehicle  : " + regNo);
                    System.out.println("   Issue    : " + a.issue);
                    System.out.println("   Status   : " + a.status);
                    break;
                }
            }
        }
        if (!found) System.out.println(" >> Appointment not found for ID: " + id);
        pause();
    }

    // ================================================================
    //                  4. SERVICE RECORD MANAGEMENT
    // ================================================================
    private static void serviceRecordMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Service Record Management ---");
            System.out.println(" 1. Add Service Record");
            System.out.println(" 2. View All Service Records");
            System.out.println(" 3. Search Service Record by ID");
            System.out.println(" 0. Back to Main Menu");

            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: addServiceRecord();        break;
                case 2: viewAllServiceRecords();   break;
                case 3: searchServiceRecordById(); break;
                case 0: back = true;               break;
                default: System.out.println("  ** Invalid choice.");
            }
        }
    }

    private static void addServiceRecord() {
        System.out.println("\n--- Add Service Record ---");
        int vehId       = readInt(" Vehicle ID                  : ");
        int apptId      = readInt(" Appointment ID (0 if none)  : ");
        String date     = readString(" Service Date (YYYY-MM-DD)   : ");
        String desc     = readString(" Work Performed / Description: ");
        double labor    = readDouble(" Labor Charges (Rs.)         : ");
        double parts    = readDouble(" Parts Cost (Rs.)            : ");
        double total    = labor + parts;
        System.out.printf(" >> Auto-calculated Total Cost: Rs. %.2f%n", total);
        String nextDate = readString(" Next Service Date (YYYY-MM-DD): ");

        if (isMysqlMode) {
            String sql = "INSERT INTO Service_Record (appointment_id, vehicle_id, service_date, service_description, labor_cost, parts_cost, total_cost, next_service_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (apptId > 0) ps.setInt(1, apptId); else ps.setNull(1, Types.INTEGER);
                ps.setInt(2, vehId); ps.setDate(3, java.sql.Date.valueOf(date)); ps.setString(4, desc);
                ps.setDouble(5, labor); ps.setDouble(6, parts); ps.setDouble(7, total); ps.setDate(8, java.sql.Date.valueOf(nextDate));
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) System.out.println(" >> Service record saved! ID: " + keys.getInt(1));
            } catch (Exception e) {
                System.out.println(" >> Error adding service record: " + e.getMessage());
            }
        } else {
            int newId = nextSrvId++;
            memSrv.add(new SrvData(newId, apptId, vehId, date, desc, labor, parts, total, nextDate));
            System.out.println(" >> Service record saved! ID: " + newId);
        }
        pause();
    }

    private static void viewAllServiceRecords() {
        System.out.println("\n--- All Service Records ---");
        String fmt = "%-5s %-15s %-12s %-25s %-10s %-10s %-10s %-12s";
        int width = 110;
        printLine(width);
        printRow(fmt, "ID", "Reg Number", "Date", "Description", "Labor", "Parts", "Total", "Next Service");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT s.service_id, v.vehicle_number, s.service_date, s.service_description, s.labor_cost, s.parts_cost, s.total_cost, s.next_service_date "
                       + "FROM Service_Record s JOIN Vehicle v ON s.vehicle_id = v.vehicle_id ORDER BY s.service_id";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    printRow(fmt, rs.getInt("service_id"), rs.getString("vehicle_number"), rs.getDate("service_date"),
                        truncate(rs.getString("service_description"), 25),
                        String.format("%.2f", rs.getDouble("labor_cost")),
                        String.format("%.2f", rs.getDouble("parts_cost")),
                        String.format("%.2f", rs.getDouble("total_cost")),
                        rs.getDate("next_service_date") != null ? rs.getDate("next_service_date") : "N/A");
                }
                if (!found) printRow("%-106s", "No service records found.");
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (SrvData s : memSrv) {
                String regNo = "Veh #" + s.vehId;
                for (VehData v : memVeh) if (v.id == s.vehId) { regNo = v.regNo; break; }
                printRow(fmt, s.id, regNo, s.date, truncate(s.description, 25),
                    String.format("%.2f", s.labor), String.format("%.2f", s.parts), String.format("%.2f", s.total), s.nextDate);
            }
        }
        printLine(width);
        pause();
    }

    private static void searchServiceRecordById() {
        System.out.println("\n--- Search Service Record by ID ---");
        int id = readInt(" Enter Service ID: ");
        boolean found = false;

        if (isMysqlMode) {
            String sql = "SELECT s.*, v.vehicle_number, v.brand, v.model FROM Service_Record s JOIN Vehicle v ON s.vehicle_id = v.vehicle_id WHERE s.service_id = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    found = true;
                    System.out.println("\n Service Record Details:");
                    System.out.println("   Service ID    : " + rs.getInt("service_id"));
                    System.out.println("   Vehicle       : " + rs.getString("vehicle_number") + " (" + rs.getString("brand") + " " + rs.getString("model") + ")");
                    System.out.println("   Date          : " + rs.getDate("service_date"));
                    System.out.println("   Description   : " + rs.getString("service_description"));
                    System.out.printf("   Labor Charges : Rs. %.2f%n", rs.getDouble("labor_cost"));
                    System.out.printf("   Parts Cost    : Rs. %.2f%n", rs.getDouble("parts_cost"));
                    System.out.printf("   Total Cost    : Rs. %.2f%n", rs.getDouble("total_cost"));
                    System.out.println("   Next Service  : " + rs.getDate("next_service_date"));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (SrvData s : memSrv) {
                if (s.id == id) {
                    found = true;
                    String regNo = "Veh #" + s.vehId, bAndM = "";
                    for (VehData v : memVeh) if (v.id == s.vehId) { regNo = v.regNo; bAndM = "(" + v.brand + " " + v.model + ")"; break; }
                    System.out.println("\n Service Record Details:");
                    System.out.println("   Service ID    : " + s.id);
                    System.out.println("   Vehicle       : " + regNo + " " + bAndM);
                    System.out.println("   Date          : " + s.date);
                    System.out.println("   Description   : " + s.description);
                    System.out.printf("   Labor Charges : Rs. %.2f%n", s.labor);
                    System.out.printf("   Parts Cost    : Rs. %.2f%n", s.parts);
                    System.out.printf("   Total Cost    : Rs. %.2f%n", s.total);
                    System.out.println("   Next Service  : " + s.nextDate);
                    break;
                }
            }
        }
        if (!found) System.out.println(" >> Service record not found for ID: " + id);
        pause();
    }

    // ================================================================
    //                     5. BILLING MANAGEMENT
    // ================================================================
    private static void billingMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Billing Management ---");
            System.out.println(" 1. Generate Bill");
            System.out.println(" 2. View All Bills");
            System.out.println(" 3. Search Bill by ID");
            System.out.println(" 0. Back to Main Menu");

            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: addBill();        break;
                case 2: viewAllBills();   break;
                case 3: searchBillById(); break;
                case 0: back = true;      break;
                default: System.out.println("  ** Invalid choice.");
            }
        }
    }

    private static void addBill() {
        System.out.println("\n--- Generate Bill ---");
        int srvId      = readInt(" Service ID                     : ");
        String date    = readString(" Bill Date (YYYY-MM-DD)         : ");
        double amount  = readDouble(" Amount (Rs.)                   : ");
        String status  = readString(" Payment Status (PAID / PENDING): ");
        String method  = readString(" Payment Method (CASH/UPI/CARD) : ");

        if (isMysqlMode) {
            String sql = "INSERT INTO Bill (service_id, bill_date, amount, payment_status, payment_method) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, srvId); ps.setDate(2, java.sql.Date.valueOf(date)); ps.setDouble(3, amount);
                ps.setString(4, status.toUpperCase()); ps.setString(5, method.toUpperCase());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) System.out.println(" >> Bill generated successfully! Bill ID: " + keys.getInt(1));
            } catch (Exception e) {
                System.out.println(" >> Error generating bill: " + e.getMessage());
            }
        } else {
            int newId = nextBillId++;
            memBill.add(new BillData(newId, srvId, date, amount, status.toUpperCase(), method.toUpperCase()));
            System.out.println(" >> Bill generated successfully! Bill ID: " + newId);
        }
        pause();
    }

    private static void viewAllBills() {
        System.out.println("\n--- All Bills ---");
        String fmt = "%-5s %-10s %-12s %-12s %-12s %-15s";
        int width = 75;
        printLine(width);
        printRow(fmt, "ID", "Service ID", "Bill Date", "Amount (Rs)", "Status", "Payment Method");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT bill_id, service_id, bill_date, amount, payment_status, payment_method FROM Bill ORDER BY bill_id";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    printRow(fmt, rs.getInt("bill_id"), rs.getInt("service_id"), rs.getDate("bill_date"),
                        String.format("%.2f", rs.getDouble("amount")), rs.getString("payment_status"), rs.getString("payment_method"));
                }
                if (!found) printRow("%-71s", "No bills found.");
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (BillData b : memBill) {
                printRow(fmt, b.id, b.srvId, b.date, String.format("%.2f", b.amount), b.status, b.method);
            }
        }
        printLine(width);
        pause();
    }

    private static void searchBillById() {
        System.out.println("\n--- Search Bill by ID ---");
        int id = readInt(" Enter Bill ID: ");
        boolean found = false;

        if (isMysqlMode) {
            String sql = "SELECT b.*, s.service_description, v.vehicle_number FROM Bill b JOIN Service_Record s ON b.service_id = s.service_id JOIN Vehicle v ON s.vehicle_id = v.vehicle_id WHERE b.bill_id = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    found = true;
                    System.out.println("\n Bill Details:");
                    System.out.println("   Bill ID        : " + rs.getInt("bill_id"));
                    System.out.println("   Service ID     : " + rs.getInt("service_id"));
                    System.out.println("   Vehicle Number : " + rs.getString("vehicle_number"));
                    System.out.println("   Service Work   : " + rs.getString("service_description"));
                    System.out.println("   Date           : " + rs.getDate("bill_date"));
                    System.out.printf("   Amount         : Rs. %.2f%n", rs.getDouble("amount"));
                    System.out.println("   Payment Status : " + rs.getString("payment_status"));
                    System.out.println("   Payment Method : " + rs.getString("payment_method"));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (BillData b : memBill) {
                if (b.id == id) {
                    found = true;
                    String srvDesc = "General Service", regNo = "N/A";
                    for (SrvData s : memSrv) if (s.id == b.srvId) {
                        srvDesc = s.description;
                        for (VehData v : memVeh) if (v.id == s.vehId) { regNo = v.regNo; break; }
                        break;
                    }
                    System.out.println("\n Bill Details:");
                    System.out.println("   Bill ID        : " + b.id);
                    System.out.println("   Service ID     : " + b.srvId);
                    System.out.println("   Vehicle Number : " + regNo);
                    System.out.println("   Service Work   : " + srvDesc);
                    System.out.println("   Date           : " + b.date);
                    System.out.printf("   Amount         : Rs. %.2f%n", b.amount);
                    System.out.println("   Payment Status : " + b.status);
                    System.out.println("   Payment Method : " + b.method);
                    break;
                }
            }
        }
        if (!found) System.out.println(" >> Bill not found for ID: " + id);
        pause();
    }

    // ================================================================
    //                        6. REPORTS
    // ================================================================
    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Reports & Analytics ---");
            System.out.println(" 1. Service History for a Vehicle");
            System.out.println(" 2. Vehicle-wise Maintenance Costs");
            System.out.println(" 3. Service Center Workload (Monthly Count)");
            System.out.println(" 0. Back to Main Menu");

            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: reportServiceHistory();        break;
                case 2: reportMaintenanceCosts();       break;
                case 3: reportServiceCenterWorkload(); break;
                case 0: back = true;                   break;
                default: System.out.println("  ** Invalid choice.");
            }
        }
    }

    private static void reportServiceHistory() {
        System.out.println("\n--- Service History for a Vehicle ---");
        String reg = readString(" Enter Vehicle Reg Number (e.g. TN 01 AB 1234): ");

        String fmt = "%-5s %-12s %-30s %-10s %-10s %-10s";
        int width = 86;
        printLine(width);
        printRow(fmt, "ID", "Date", "Description", "Labor", "Parts", "Total");
        printLine(width);

        double grandTotal = 0;
        int count = 0;

        if (isMysqlMode) {
            String sql = "SELECT s.service_id, s.service_date, s.service_description, s.labor_cost, s.parts_cost, s.total_cost "
                       + "FROM Service_Record s JOIN Vehicle v ON s.vehicle_id = v.vehicle_id WHERE UPPER(v.vehicle_number) = UPPER(?) ORDER BY s.service_date";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, reg);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    count++;
                    double tot = rs.getDouble("total_cost");
                    grandTotal += tot;
                    printRow(fmt, rs.getInt("service_id"), rs.getDate("service_date"),
                        truncate(rs.getString("service_description"), 30),
                        String.format("%.2f", rs.getDouble("labor_cost")),
                        String.format("%.2f", rs.getDouble("parts_cost")),
                        String.format("%.2f", tot));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            int targetVehId = -1;
            for (VehData v : memVeh) if (v.regNo.equalsIgnoreCase(reg)) { targetVehId = v.id; break; }
            if (targetVehId != -1) {
                for (SrvData s : memSrv) {
                    if (s.vehId == targetVehId) {
                        count++;
                        grandTotal += s.total;
                        printRow(fmt, s.id, s.date, truncate(s.description, 30),
                            String.format("%.2f", s.labor), String.format("%.2f", s.parts), String.format("%.2f", s.total));
                    }
                }
            }
        }

        if (count == 0) printRow("%-82s", "No service records found for " + reg);
        printLine(width);
        System.out.printf(" Total Services: %d | Total Spent: Rs. %.2f%n", count, grandTotal);
        pause();
    }

    private static void reportMaintenanceCosts() {
        System.out.println("\n--- Vehicle-Wise Maintenance Costs ---");
        String fmt = "%-16s %-14s %-14s %-10s %-16s";
        int width = 78;
        printLine(width);
        printRow(fmt, "Reg Number", "Brand", "Model", "Services", "Total Cost (Rs)");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT v.vehicle_number, v.brand, v.model, COUNT(s.service_id) AS service_count, COALESCE(SUM(s.total_cost), 0) AS total_maint_cost "
                       + "FROM Vehicle v LEFT JOIN Service_Record s ON v.vehicle_id = s.vehicle_id GROUP BY v.vehicle_id, v.vehicle_number, v.brand, v.model ORDER BY total_maint_cost DESC";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    printRow(fmt, rs.getString("vehicle_number"), rs.getString("brand"), rs.getString("model"),
                        rs.getInt("service_count"), String.format("%.2f", rs.getDouble("total_maint_cost")));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            for (VehData v : memVeh) {
                int count = 0; double total = 0;
                for (SrvData s : memSrv) if (s.vehId == v.id) { count++; total += s.total; }
                printRow(fmt, v.regNo, v.brand, v.model, count, String.format("%.2f", total));
            }
        }
        printLine(width);
        pause();
    }

    private static void reportServiceCenterWorkload() {
        System.out.println("\n--- Service Center Workload (Monthly Service Count) ---");
        String fmt = "%-12s %-15s %-18s";
        int width = 50;
        printLine(width);
        printRow(fmt, "Month", "Service Count", "Revenue (Rs)");
        printLine(width);

        if (isMysqlMode) {
            String sql = "SELECT DATE_FORMAT(service_date, '%Y-%m') AS month_year, COUNT(*) AS srv_count, SUM(total_cost) AS total_rev "
                       + "FROM Service_Record GROUP BY month_year ORDER BY month_year";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    printRow(fmt, rs.getString("month_year"), rs.getInt("srv_count"), String.format("%.2f", rs.getDouble("total_rev")));
                }
            } catch (SQLException e) {
                System.out.println(" >> Error: " + e.getMessage());
            }
        } else {
            Map<String, int[]> monthly = new TreeMap<>();
            for (SrvData s : memSrv) {
                String ym = s.date.length() >= 7 ? s.date.substring(0, 7) : s.date;
                monthly.putIfAbsent(ym, new int[]{0, 0});
                monthly.get(ym)[0]++;
                monthly.get(ym)[1] += (int) s.total;
            }
            for (Map.Entry<String, int[]> e : monthly.entrySet()) {
                printRow(fmt, e.getKey(), e.getValue()[0], String.format("%.2f", (double) e.getValue()[1]));
            }
        }
        printLine(width);
        pause();
    }
}
