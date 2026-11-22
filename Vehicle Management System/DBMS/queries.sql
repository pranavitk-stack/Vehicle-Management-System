-- ============================================================
-- Vehicle Service Management and Maintenance Analysis System
-- SQL Queries (15+ queries)
-- ============================================================

USE VehicleServiceDB;

-- ============================================================
-- Q1. Basic SELECT – List all customers
-- ============================================================
SELECT * FROM Customer;

-- ============================================================
-- Q2. Basic INSERT – Add a new customer
-- ============================================================
INSERT INTO Customer (customer_name, phone, email, address)
VALUES ('Deepa Venkatesh', '9001234567', 'deepa.v@gmail.com',
        '89, MG Road, Thanjavur, Tamil Nadu 613001');

-- ============================================================
-- Q3. Basic UPDATE – Update a customer's phone number
-- ============================================================
UPDATE Customer
SET    phone = '9112233445'
WHERE  customer_id = 3;

-- ============================================================
-- Q4. Basic DELETE – Remove a cancelled appointment
-- ============================================================
DELETE FROM Appointment
WHERE  appointment_id = 8
  AND  status = 'BOOKED';

-- ============================================================
-- Q5. INNER JOIN – Service history for a specific vehicle
--     (Vehicle number: TN 01 AB 1234)
-- ============================================================
SELECT v.vehicle_number,
       v.brand,
       v.model,
       sr.service_date,
       sr.service_description,
       sr.labor_cost,
       sr.parts_cost,
       sr.total_cost
FROM   Service_Record sr
       INNER JOIN Vehicle v ON sr.vehicle_id = v.vehicle_id
WHERE  v.vehicle_number = 'TN 01 AB 1234'
ORDER  BY sr.service_date DESC;

-- ============================================================
-- Q6. LEFT JOIN – All customers with their vehicles
--     (includes customers who have no vehicles yet)
-- ============================================================
SELECT c.customer_id,
       c.customer_name,
       c.phone,
       v.vehicle_number,
       v.brand,
       v.model
FROM   Customer c
       LEFT JOIN Vehicle v ON c.customer_id = v.customer_id
ORDER  BY c.customer_name;

-- ============================================================
-- Q7. Aggregate – Vehicle-wise total maintenance cost
-- ============================================================
SELECT v.vehicle_number,
       v.brand,
       v.model,
       COUNT(sr.service_id)   AS total_services,
       SUM(sr.total_cost)     AS total_maintenance_cost
FROM   Vehicle v
       INNER JOIN Service_Record sr ON v.vehicle_id = sr.vehicle_id
GROUP  BY v.vehicle_id, v.vehicle_number, v.brand, v.model
ORDER  BY total_maintenance_cost DESC;

-- ============================================================
-- Q8. GROUP BY + HAVING – Vehicles with maintenance cost > 5000
-- ============================================================
SELECT v.vehicle_number,
       v.brand,
       v.model,
       SUM(sr.total_cost) AS total_maintenance_cost
FROM   Vehicle v
       INNER JOIN Service_Record sr ON v.vehicle_id = sr.vehicle_id
GROUP  BY v.vehicle_id, v.vehicle_number, v.brand, v.model
HAVING SUM(sr.total_cost) > 5000
ORDER  BY total_maintenance_cost DESC;

-- ============================================================
-- Q9. Aggregate – Monthly service count
-- ============================================================
SELECT DATE_FORMAT(service_date, '%Y-%m')   AS service_month,
       MONTHNAME(service_date)              AS month_name,
       COUNT(*)                             AS service_count,
       SUM(total_cost)                      AS monthly_revenue
FROM   Service_Record
GROUP  BY service_month, month_name
ORDER  BY service_month;

-- ============================================================
-- Q10. Aggregate – Most expensive single service
-- ============================================================
SELECT sr.service_id,
       v.vehicle_number,
       sr.service_description,
       sr.total_cost
FROM   Service_Record sr
       INNER JOIN Vehicle v ON sr.vehicle_id = v.vehicle_id
WHERE  sr.total_cost = (SELECT MAX(total_cost) FROM Service_Record);

-- ============================================================
-- Q11. Subquery – Pending payments (unpaid bills with details)
-- ============================================================
SELECT b.bill_id,
       b.bill_date,
       b.amount,
       b.payment_status,
       sr.service_description,
       v.vehicle_number,
       c.customer_name,
       c.phone
FROM   Bill b
       INNER JOIN Service_Record sr ON b.service_id = sr.service_id
       INNER JOIN Vehicle v        ON sr.vehicle_id = v.vehicle_id
       INNER JOIN Customer c       ON v.customer_id = c.customer_id
WHERE  b.payment_status = 'PENDING'
ORDER  BY b.bill_date;

-- ============================================================
-- Q12. Subquery – Customers who have spent more than the
--      average total bill amount
-- ============================================================
SELECT c.customer_id,
       c.customer_name,
       SUM(b.amount) AS total_spent
FROM   Customer c
       INNER JOIN Vehicle v        ON c.customer_id = v.customer_id
       INNER JOIN Service_Record sr ON v.vehicle_id  = sr.vehicle_id
       INNER JOIN Bill b           ON sr.service_id  = b.service_id
GROUP  BY c.customer_id, c.customer_name
HAVING SUM(b.amount) > (SELECT AVG(amount) FROM Bill)
ORDER  BY total_spent DESC;

-- ============================================================
-- Q13. Frequently serviced vehicles (more than 2 services)
-- ============================================================
SELECT v.vehicle_number,
       v.brand,
       v.model,
       c.customer_name,
       COUNT(sr.service_id) AS service_count
FROM   Vehicle v
       INNER JOIN Service_Record sr ON v.vehicle_id  = sr.vehicle_id
       INNER JOIN Customer c       ON v.customer_id = c.customer_id
GROUP  BY v.vehicle_id, v.vehicle_number, v.brand, v.model, c.customer_name
HAVING COUNT(sr.service_id) > 2
ORDER  BY service_count DESC;

-- ============================================================
-- Q14. Date function – Services due for follow-up in the
--      next 30 days
-- ============================================================
SELECT sr.service_id,
       v.vehicle_number,
       v.brand,
       v.model,
       sr.service_description,
       sr.next_service_date,
       DATEDIFF(sr.next_service_date, CURDATE()) AS days_remaining
FROM   Service_Record sr
       INNER JOIN Vehicle v ON sr.vehicle_id = v.vehicle_id
WHERE  sr.next_service_date IS NOT NULL
  AND  sr.next_service_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER  BY sr.next_service_date;

-- ============================================================
-- Q15. AVG and MIN – Average and minimum service cost
--      per vehicle type
-- ============================================================
SELECT v.vehicle_type,
       COUNT(sr.service_id)          AS total_services,
       ROUND(AVG(sr.total_cost), 2)  AS avg_cost,
       MIN(sr.total_cost)            AS min_cost,
       MAX(sr.total_cost)            AS max_cost
FROM   Vehicle v
       INNER JOIN Service_Record sr ON v.vehicle_id = sr.vehicle_id
GROUP  BY v.vehicle_type
ORDER  BY avg_cost DESC;

-- ============================================================
-- Q16. JOIN with ORDER BY – Complete appointment details
--      sorted by date
-- ============================================================
SELECT a.appointment_id,
       a.appointment_date,
       a.status,
       c.customer_name,
       v.vehicle_number,
       v.brand,
       v.model,
       a.issue_description
FROM   Appointment a
       INNER JOIN Customer c ON a.customer_id = c.customer_id
       INNER JOIN Vehicle v  ON a.vehicle_id  = v.vehicle_id
ORDER  BY a.appointment_date DESC;

-- ============================================================
-- Q17. Revenue summary – Total revenue, paid, and pending
-- ============================================================
SELECT COUNT(*)                                                          AS total_bills,
       SUM(amount)                                                       AS total_revenue,
       SUM(CASE WHEN payment_status = 'PAID'    THEN amount ELSE 0 END) AS total_paid,
       SUM(CASE WHEN payment_status = 'PENDING' THEN amount ELSE 0 END) AS total_pending
FROM   Bill;

-- ============================================================
-- Q18. Date function – Days since last service per vehicle
-- ============================================================
SELECT v.vehicle_number,
       v.brand,
       v.model,
       MAX(sr.service_date)                        AS last_service_date,
       DATEDIFF(CURDATE(), MAX(sr.service_date))   AS days_since_service
FROM   Vehicle v
       INNER JOIN Service_Record sr ON v.vehicle_id = sr.vehicle_id
GROUP  BY v.vehicle_id, v.vehicle_number, v.brand, v.model
ORDER  BY days_since_service DESC;

-- ============================================================
-- End of Queries
-- ============================================================
