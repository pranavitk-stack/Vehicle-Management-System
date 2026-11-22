-- ============================================================
-- Vehicle Service Management and Maintenance Analysis System
-- Sample Data (Realistic Indian / Tamil Nadu data)
-- ============================================================

USE VehicleServiceDB;

-- ============================================================
-- 1. Customers (5 records)
-- ============================================================
INSERT INTO Customer (customer_name, phone, email, address) VALUES
('Rajesh Kumar',      '9876543210', 'rajesh.kumar@gmail.com',    '12, Anna Nagar, Chennai, Tamil Nadu 600040'),
('Priya Sharma',      '9845612378', 'priya.sharma@yahoo.com',    '45, Gandhi Road, Coimbatore, Tamil Nadu 641001'),
('Murugan Selvam',    '9123456789', 'murugan.s@outlook.com',     '78, Kamarajar Salai, Madurai, Tamil Nadu 625001'),
('Lakshmi Narayanan', '9988776655', 'lakshmi.n@gmail.com',       '23, Nehru Street, Trichy, Tamil Nadu 620001'),
('Karthik Sundaram',  '9871234560', 'karthik.sundaram@gmail.com','56, Bharathi Nagar, Salem, Tamil Nadu 636001');

-- ============================================================
-- 2. Vehicles (6 records – Tamil Nadu registration numbers)
-- ============================================================
INSERT INTO Vehicle (customer_id, vehicle_number, brand, model, vehicle_type, manufacture_year) VALUES
(1, 'TN 01 AB 1234', 'Maruti Suzuki', 'Swift',          'Hatchback', 2020),
(1, 'TN 01 CD 5678', 'Hyundai',       'Creta',          'SUV',       2022),
(2, 'TN 38 EF 9012', 'Tata',          'Nexon',          'SUV',       2021),
(3, 'TN 59 GH 3456', 'Honda',         'City',           'Sedan',     2019),
(4, 'TN 45 IJ 7890', 'Toyota',        'Innova Crysta',  'MUV',       2023),
(5, 'TN 30 KL 2345', 'Mahindra',      'Thar',           'SUV',       2022);

-- ============================================================
-- 3. Appointments (8 records)
-- ============================================================
INSERT INTO Appointment (customer_id, vehicle_id, appointment_date, issue_description, status) VALUES
(1, 1, '2025-01-10', 'Engine oil change and general checkup',                      'COMPLETED'),
(1, 2, '2025-02-15', 'AC not cooling properly',                                     'COMPLETED'),
(2, 3, '2025-02-20', 'Brake pads worn out, squeaking noise while braking',          'COMPLETED'),
(3, 4, '2025-03-05', 'Clutch plate replacement needed',                             'COMPLETED'),
(4, 5, '2025-03-18', 'Periodic maintenance – 10,000 km service',                   'COMPLETED'),
(5, 6, '2025-04-02', 'Front suspension noise on rough roads',                       'COMPLETED'),
(1, 1, '2025-05-10', 'Tyre rotation and wheel alignment',                           'BOOKED'),
(3, 4, '2025-05-20', 'Battery replacement and electrical system check',             'BOOKED');

-- ============================================================
-- 4. Service Records (10 records)
-- ============================================================
INSERT INTO Service_Record (appointment_id, vehicle_id, service_date, service_description, labor_cost, parts_cost, total_cost, next_service_date) VALUES
(1, 1, '2025-01-10', 'Engine oil change (fully synthetic 5W-30) and oil filter replacement',          500.00,  1800.00,  2300.00, '2025-07-10'),
(1, 1, '2025-01-10', 'General checkup – air filter cleaning, coolant top-up, battery check',         400.00,   300.00,   700.00,  NULL),
(2, 2, '2025-02-16', 'AC gas refill (R-134a) and compressor belt adjustment',                        800.00,  1500.00,  2300.00, '2026-02-16'),
(3, 3, '2025-02-21', 'Front and rear brake pad replacement with rotor resurfacing',                   1200.00, 3500.00,  4700.00, '2025-08-21'),
(4, 4, '2025-03-06', 'Clutch plate and pressure plate replacement',                                  2500.00, 6500.00,  9000.00, '2026-03-06'),
(5, 5, '2025-03-18', '10,000 km periodic service – oil change, filter replacement, tyre rotation',   600.00,  2200.00,  2800.00, '2025-09-18'),
(5, 5, '2025-03-18', 'Wheel alignment and balancing',                                                400.00,   200.00,   600.00,  NULL),
(6, 6, '2025-04-03', 'Front suspension bush and shock absorber replacement',                          1800.00, 4500.00,  6300.00, '2025-10-03'),
(NULL, 1, '2025-04-15', 'Emergency coolant leak repair and radiator hose replacement',                1000.00, 1200.00,  2200.00, '2025-10-15'),
(NULL, 3, '2025-04-28', 'Battery replacement (Amaron 55 Ah) and terminal cleaning',                   300.00,  4200.00,  4500.00, '2027-04-28');

-- ============================================================
-- 5. Bills (8 records)
-- ============================================================
INSERT INTO Bill (service_id, bill_date, amount, payment_status, payment_method) VALUES
(1,  '2025-01-10', 2300.00, 'PAID',    'UPI'),
(2,  '2025-01-10',  700.00, 'PAID',    'CASH'),
(3,  '2025-02-16', 2300.00, 'PAID',    'CARD'),
(4,  '2025-02-21', 4700.00, 'PAID',    'UPI'),
(5,  '2025-03-06', 9000.00, 'PAID',    'CARD'),
(6,  '2025-03-18', 2800.00, 'PENDING', NULL),
(8,  '2025-04-03', 6300.00, 'PENDING', NULL),
(9,  '2025-04-15', 2200.00, 'PENDING', NULL);

-- ============================================================
-- End of Sample Data
-- ============================================================
