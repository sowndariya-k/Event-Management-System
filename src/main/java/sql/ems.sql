-- create database
CREATE DATABASE  IF NOT EXISTS `event_management_system` ;

-- use database 
use `event_management_system`;
-- 
-- roles table structure
--
DROP TABLE IF EXISTS `roles`;
-- create roles table
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ;

-- Insert ems roles
LOCK TABLES `roles` WRITE;
INSERT INTO `roles` VALUES (1,'ADMIN',1,'2026-01-29 08:40:40'),(2,'ATTENDEE',1,'2026-01-29 08:40:40'),(3,'ORGANIZER',1,'2026-01-29 08:40:40');

UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `roles`;
-- 
-- user table structure
--
-- drop if exist user table
DROP TABLE IF EXISTS `users`;

-- create user table
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone` varchar(15) DEFAULT NULL,
  `gender` varchar(15) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role_id` int NOT NULL,
  `status` ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
   `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
   `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `failed_attempts` int DEFAULT '0',
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ;

-- Insert one admin user
INSERT INTO `users` 
(`user_id`, `full_name`, `email`, `phone`, `gender`, `password_hash`, `role_id`, `status`, `created_at`, `updated_at`, `failed_attempts`, `last_login`)
VALUES 
(1, 'ADMIN 01', 'admin@ems.com', '7878787878', 'Male', '$2a$12$N75EBBWWmJwdBf1fJyVoheUn6ghZau9JwzO0uDBGh4WUXIvjWHu0C', 1, 'ACTIVE', '2026-01-29 14:11:05', NULL, 0, '2026-03-02 16:18:51');
-- Insert one user
INSERT INTO `users`
(`full_name`, `email`, `phone`, `gender`, `password_hash`, `role_id`)
VALUES
('Sowndariya', 'sowndariya@gmail.com', '6374867255', 'Female', '$2a$12$bKQzR3uH6b3aX0jbVYwNqObF/u0PCZ9PDAxueWCO7z8QwS5bnjVQa', 2);

-- Check inserted data
SELECT * FROM `users`;
-- 
-- categories table structure
--
-- Drop table if exists
DROP TABLE IF EXISTS `categories`;

-- Create table
CREATE TABLE `categories` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `unique_category_name` (`name`)
);
-- Insert sample data
LOCK TABLES `categories` WRITE;
INSERT INTO `categories` (`category_id`, `name`, `is_active`) VALUES
(1, 'Technology', 1),
(2, 'Music', 1),
(3, 'Business', 1),
(4, 'Health & Wellness', 1),
(5, 'Education', 1),
(6, 'Arts & Culture', 1),
(7, 'Sports', 1),
(8, 'Food & Beveragecategories', 1),
(9, 'Networking', 1),
(10, 'Entertainment', 1),
(11, 'Infotainment', 0);
UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `categories`;
-- 
-- venue table structure
--
-- Drop table if it exists
DROP TABLE IF EXISTS `venues`;

-- Create table
CREATE TABLE `venues` (
  `venue_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `street` VARCHAR(100) NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `state` VARCHAR(50) NOT NULL,
  `pincode` VARCHAR(10) NOT NULL,
  `max_capacity` INT NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`venue_id`),
  UNIQUE KEY `unique_venue_address` (`name`, `street`),
  KEY `city_idx` (`city`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insert sample data
LOCK TABLES `venues` WRITE;
INSERT INTO `venues` (`venue_id`, `name`, `street`, `city`, `state`, `pincode`, `max_capacity`, `is_active`, `created_at`, `updated_at`) VALUES
(1,'Chennai Trade Centre','1 Nandambakkam Main Rd','Chennai','Tamil Nadu','600089',5000,1,'2026-01-01 08:00:00',NULL),
(2,'ITC Grand Chola Ballroom','63 Mount Road','Chennai','Tamil Nadu','600032',800,1,'2026-01-01 08:00:00',NULL),
(3,'CODISSIA Trade Fair Complex','1050 Avinashi Rd','Coimbatore','Tamil Nadu','641014',3000,1,'2026-01-01 08:00:00',NULL),
(4,'Trichy Rockfort Convention','12 Rockfort Road','Trichy','Tamil Nadu','620001',1200,1,'2026-01-01 08:00:00',NULL),
(5,'Madurai Convention Centre','5 Bypass Road','Madurai','Tamil Nadu','625016',2000,1,'2026-01-01 08:00:00',NULL),
(6,'Anna Centenary Library Hall','Kotturpuram','Chennai','Tamil Nadu','600085',600,1,'2026-01-01 08:00:00',NULL),
(7,'Nehru Indoor Stadium','Periyamet','Chennai','Tamil Nadu','600003',4000,1,'2026-01-01 08:00:00',NULL),
(8,'PSG Tech Auditorium','Peelamedu','Coimbatore','Tamil Nadu','641004',1000,1,'2026-01-01 08:00:00',NULL),
(9,'Vels University Seminar Hall','1 Velan Nagar, Pallavaram','Chennai','Tamil Nadu','600117',400,1,'2026-01-01 08:00:00',NULL),
(10,'The Residency Banquet Hall','49 GN Chetty Road','Chennai','Tamil Nadu','600017',500,1,'2026-01-01 08:00:00',NULL);
UNLOCK TABLES;

-- Reference query to check data
SELECT * FROM `venues`;
-- 
-- event table structure
--
-- Drop table if exists
DROP TABLE IF EXISTS `events`;

-- Create table
CREATE TABLE `events` (
  `event_id` INT NOT NULL AUTO_INCREMENT,
  `organizer_id` INT NOT NULL,
  `title` VARCHAR(150) NOT NULL,
  `description` TEXT,
  `category_id` INT NOT NULL,
  `venue_id` INT NOT NULL,
  `start_datetime` DATETIME NOT NULL,
  `end_datetime` DATETIME NOT NULL,
  `capacity` INT NOT NULL,
  `status` ENUM('DRAFT','PUBLISHED','APPROVED','CANCELLED','COMPLETED') NOT NULL DEFAULT 'DRAFT',
  `approved_by` INT DEFAULT NULL,
  `approved_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`event_id`),
  KEY `organizer_id` (`organizer_id`),
  KEY `category_id` (`category_id`),
  KEY `venue_id` (`venue_id`),
  KEY `approved_by` (`approved_by`),
  CONSTRAINT `events_ibfk_1` FOREIGN KEY (`organizer_id`) REFERENCES `users`(`user_id`),
  CONSTRAINT `events_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories`(`category_id`),
  CONSTRAINT `events_ibfk_3` FOREIGN KEY (`venue_id`) REFERENCES `venues`(`venue_id`),
  CONSTRAINT `events_ibfk_4` FOREIGN KEY (`approved_by`) REFERENCES `users`(`user_id`)
);

-- Insert example data (just one event as reference)
LOCK TABLES `events` WRITE;

INSERT INTO `events` 
(`event_id`, `organizer_id`, `title`, `description`, `category_id`, `venue_id`, `start_datetime`, `end_datetime`, `capacity`, `status`, `approved_by`, `approved_at`, `created_at`, `updated_at`)
VALUES 
(1, 3, 'AI & Machine Learning Summit 2026', 'Deep-dive into the latest advances in AI and ML with industry experts from across India.', 1, 1, '2026-04-10 09:00:00', '2026-04-10 18:00:00', 1000, 'DRAFT', NULL, NULL, '2026-02-01 10:00:00', CURRENT_TIMESTAMP),
(38, 5, 'AI in Healthcare Conference', 'Explore AI applications transforming healthcare, diagnostics, and patient care with expert sessions.', 1, 1, '2026-06-05 09:00:00', '2026-06-05 18:00:00', 250, 'DRAFT', NULL, NULL, NOW(), CURRENT_TIMESTAMP),
(39, 3, 'Sustainable Energy Summit', 'Panels and workshops on renewable energy, green tech, and sustainable business practices.', 2, 2, '2026-06-10 10:00:00', '2026-06-10 17:00:00', 300, 'DRAFT', NULL, NULL, NOW(), CURRENT_TIMESTAMP),
(40, 3, 'Startup Pitch Night', 'Early-stage startups pitch to investors, receive feedback and networking opportunities.', 3, 3, '2026-06-15 18:00:00', '2026-06-15 21:00:00', 150, 'DRAFT', NULL, NULL, NOW(), CURRENT_TIMESTAMP),
(41, 5, 'Madurai Literature Festival', 'Celebration of regional literature with author talks, book launches, and workshops.', 6, 5, '2026-06-20 09:00:00', '2026-06-21 18:00:00', 200, 'DRAFT', NULL, NULL, NOW(), CURRENT_TIMESTAMP),
(42, 3, 'Blockchain Expo 2026', 'Explore blockchain innovations and Web3 applications.', '1', '2', '2026-07-01 09:00:00', '2026-07-01 17:00:00', '500', 'PUBLISHED', '1', '2026-03-26 15:09:37', '2026-03-26 15:09:37', '2026-03-26 15:09:37'),
(43, 6, 'Cybersecurity & Privacy Forum', 'Discussions on data privacy, ethical hacking, compliance, and corporate cybersecurity strategies.', 1, 2, '2026-01-05 09:00:00', '2026-02-25 17:00:00', 400, 'DRAFT', NULL, NULL, NOW(), CURRENT_TIMESTAMP),(42, 3, 'Blockchain Expo 2026', 'Explore blockchain innovations and Web3 applications.', 1, 2, '2026-07-01 09:00:00', '2026-07-01 17:00:00', 500, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(45, 5, 'Photography Masterclass', 'Hands-on photography training with experts.', 6, 6, '2026-07-05 10:00:00', '2026-07-05 16:00:00', 200, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(46, 5, 'Fitness Bootcamp', 'High intensity training and wellness sessions.', 4, 7, '2026-07-10 06:00:00', '2026-07-10 09:00:00', 300, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(47, 3, 'Startup Networking Meetup', 'Connect with investors and founders.', 9, 10, '2026-07-12 18:00:00', '2026-07-12 21:00:00', 150, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(48, 6, 'Food Carnival Chennai', 'Explore diverse cuisines and live cooking shows.', 8, 1, '2026-07-15 11:00:00', '2026-07-15 20:00:00', 1000, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(49, 3, 'Cybersecurity Workshop', 'Learn ethical hacking and security practices.', 1, 2, '2026-07-18 09:00:00', '2026-07-18 17:00:00', 250, 'DRAFT', NULL, NULL, NOW(), CURRENT_TIMESTAMP),
(50, 5, 'Music Night Live', 'Live concert with top bands.', 2, 7, '2026-07-20 18:00:00', '2026-07-20 22:00:00', 3000, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(51, 3, 'Business Growth Summit', 'Strategies for scaling businesses.', 3, 3, '2026-07-22 09:00:00', '2026-07-22 17:00:00', 400, 'APPROVED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(52, 6, 'Art & Culture Fest', 'Celebrating traditional and modern art.', 6, 5, '2026-07-25 10:00:00', '2026-07-26 18:00:00', 600, 'PUBLISHED', 1, NOW(), NOW(), CURRENT_TIMESTAMP),
(53, 5, 'Yoga Retreat', 'Relaxation and mindfulness sessions.', 4, 6, '2026-07-28 06:00:00', '2026-07-28 12:00:00', 200, 'CANCELLED', NULL, NULL, NOW(), CURRENT_TIMESTAMP);
SELECT * FROM `events`;
-- approve
SELECT * 
FROM events
WHERE TRIM(status) = 'DRAFT'
  AND start_datetime > UTC_TIMESTAMP()
  AND approved_at IS NULL;
-- cancel
UPDATE events SET status = 'CANCELLED' WHERE event_id = 1;

-- event table structure
--
-- Drop table if exists
DROP TABLE IF EXISTS `feedback`;

-- Create table
CREATE TABLE `feedback` (
  `feedback_id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `rating` INT DEFAULT NULL,
  `comments` TEXT,
  `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  UNIQUE KEY `unique_user_event` (`event_id`,`user_id`),
  KEY `event_id` (`event_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events`(`event_id`),
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- Insert example feedback (just one record for reference)
LOCK TABLES `feedback` WRITE;
INSERT INTO `feedback` 
(`feedback_id`, `event_id`, `user_id`, `rating`, `comments`, `submitted_at`)
VALUES 
(1, 1, 2, 5, 'Excellent bootcamp! The hands-on sessions were incredibly practical.', '2026-01-20 10:00:00');
UNLOCK TABLES;


-- Check inserted data
SELECT * FROM `feedback`;
--
-- notifications table strructure
--
-- Drop table if exists
DROP TABLE IF EXISTS `notifications`;

-- Create table
CREATE TABLE `notifications` (
  `notification_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `message` TEXT NOT NULL,
  `type` VARCHAR(30) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_status` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`notification_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- Insert example notification (just one record for reference)
LOCK TABLES `notifications` WRITE;
INSERT INTO `notifications` 
(`notification_id`, `user_id`, `message`, `type`, `created_at`, `read_status`)
VALUES 
(1, 5, 'Your event "Chennai Tech Fest 2026" has been published successfully.', 'EVENT_PUBLISHED', '2026-02-08 09:05:00', 1);
UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `notifications`;
--
-- offer  table structure
--
-- Drop the table if it exists
DROP TABLE IF EXISTS `offers`;

-- Create the offers table
CREATE TABLE `offers` (
  `offer_id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `code` VARCHAR(30) NOT NULL,
  `discount_percentage` INT DEFAULT NULL,
  `valid_from` DATETIME DEFAULT NULL,
  `valid_to` DATETIME DEFAULT NULL,
  PRIMARY KEY (`offer_id`),
  UNIQUE KEY `unique_event_code` (`event_id`,`code`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `offers_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events`(`event_id`)
);

-- Insert sample offers
LOCK TABLES `offers` WRITE;
INSERT INTO `offers` 
(`offer_id`, `event_id`, `code`, `discount_percentage`, `valid_from`, `valid_to`)
VALUES 
(1, 1, 'TECHFEST20', 20, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
(2, 1, 'NEWCODE10', 10, '2026-01-01 00:00:00', '2026-12-31 23:59:59');
UNLOCK TABLES;

-- Verify inserted data
SELECT * FROM `offers`;
--
-- registration table strructure
--
-- Drop table if exists
DROP TABLE IF EXISTS `registrations`;

-- Create table
CREATE TABLE `registrations` (
  `registration_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `event_id` INT NOT NULL,
  `registration_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('CONFIRMED','CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
  PRIMARY KEY (`registration_id`),
  KEY `user_id` (`user_id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `registrations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
  CONSTRAINT `registrations_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `events`(`event_id`)
);

-- Example insert (just one record for reference)
LOCK TABLES `registrations` WRITE;
INSERT INTO `registrations` 
(`registration_id`, `user_id`, `event_id`, `registration_date`, `status`)
VALUES 
(1, 2, 1, '2026-01-05 10:15:00', 'CONFIRMED');
UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `registrations`;
--
-- offer usage table structure
--
-- Drop table if exists
DROP TABLE IF EXISTS `offer_usages`;

-- Create table
CREATE TABLE `offer_usages` (
  `offer_usage_id` INT NOT NULL AUTO_INCREMENT,
  `offer_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `registration_id` INT NOT NULL,
  `used_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`offer_usage_id`),
  UNIQUE KEY `unique_offer_user` (`offer_id`,`user_id`),
  KEY `user_id` (`user_id`),
  KEY `registration_id` (`registration_id`),
  CONSTRAINT `offer_usages_ibfk_1` FOREIGN KEY (`offer_id`) REFERENCES `offers`(`offer_id`),
  CONSTRAINT `offer_usages_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
  CONSTRAINT `offer_usages_ibfk_3` FOREIGN KEY (`registration_id`) REFERENCES `registrations`(`registration_id`)
);

-- Example insert (just one usage for reference)
LOCK TABLES `offer_usages` WRITE;
INSERT INTO `offer_usages` 
(`offer_usage_id`, `offer_id`, `user_id`, `registration_id`, `used_at`)
VALUES 
(1, 1, 2, 1, '2026-03-02 12:00:00');
UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `offer_usages`;

--
-- ticket table structure
--
-- Drop table if exists
DROP TABLE IF EXISTS `tickets`;

-- Create table
CREATE TABLE `tickets` (
  `ticket_id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `ticket_type` VARCHAR(50) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `total_quantity` INT NOT NULL,
  `available_quantity` INT NOT NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events`(`event_id`)
) ;

-- Insert example records
LOCK TABLES `tickets` WRITE;
INSERT INTO `tickets` (`ticket_id`, `event_id`, `ticket_type`, `price`, `total_quantity`, `available_quantity`) VALUES
(1,40,'General Admission',499.00,3000,2999),
(2,41,'General Admission',799.00,2000,2000),
(3,1,'Delegate Pass',999.00,500,500),
(4, 39, 'Regular', 500, 100, 100),
(5, 39, 'VIP', 1000, 50, 50),
(38, 'General', 500.00, 250, 250),
(42, 'General', 750.00, 500, 500),
(45, 'General', 600.00, 200, 200),
(46, 'General', 300.00, 300, 300),
(47, 'General', 400.00, 150, 150),
(48, 'General', 700.00, 1000, 1000),
(50, 'General', 800.00, 3000, 3000),
(52, 'General', 450.00, 600, 600);
UNLOCK TABLES;
-- Reference query to check data
SELECT * FROM `tickets`;
--
-- registration ticket table strructure
--
-- Drop table if exists
DROP TABLE IF EXISTS `registration_tickets`;

-- Create table
CREATE TABLE `registration_tickets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `registration_id` INT NOT NULL,
  `ticket_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `registration_id` (`registration_id`),
  KEY `ticket_id` (`ticket_id`),
  CONSTRAINT `registration_tickets_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registrations`(`registration_id`),
  CONSTRAINT `registration_tickets_ibfk_2` FOREIGN KEY (`ticket_id`) REFERENCES `tickets`(`ticket_id`)
);

-- Insert example record (just one for reference)
LOCK TABLES `registration_tickets` WRITE;
INSERT INTO `registration_tickets` 
(`id`, `registration_id`, `ticket_id`, `quantity`)
VALUES 
(1, 1, 3, 1);
UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `registration_tickets`;
--
-- payment table structure
--
-- Drop table if exists
DROP TABLE IF EXISTS `payments`;

-- Create table
CREATE TABLE `payments` (
  `payment_id` INT NOT NULL AUTO_INCREMENT,
  `registration_id` INT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_method` VARCHAR(30) NOT NULL,
  `payment_status` ENUM('SUCCESS','FAILED','PENDING') NOT NULL DEFAULT 'SUCCESS',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `offer_id` INT DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `registration_id` (`registration_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registrations`(`registration_id`)
);

-- Insert example record (just one for reference)
LOCK TABLES `payments` WRITE;
INSERT INTO `payments` 
(`payment_id`, `registration_id`, `amount`, `payment_method`, `payment_status`, `created_at`, `offer_id`)
VALUES 
(1, 1, 3999.00, 'CARD', 'SUCCESS', '2026-01-05 10:16:00', NULL);
UNLOCK TABLES;

-- Check inserted data
SELECT * FROM `payments`;


--
-- Dumping routines for database 'event_management_db'
--
DELIMITER ;;

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_register_for_event`(
-- INPUT PARAMETERS
    IN p_user_id INT,
    IN p_event_id INT,
    IN p_ticket_id INT,
    IN p_quantity INT,
    IN p_price DECIMAL(10,2),
    IN p_payment_method VARCHAR(30),
    IN p_offer_code VARCHAR(50),
    
    -- OUTPUT PARAMETERS
    
    OUT o_success BOOLEAN,
    OUT o_message VARCHAR(255),
    OUT o_registration_id INT,
    OUT o_final_amount DECIMAL(10,2)
)
proc_block: BEGIN
 /*
        Local variables used only inside this procedure.
        These keep intermediate state so that Java does not
        have to manage partial results.
    */
    DECLARE v_available INT;
    DECLARE v_offer_id INT DEFAULT NULL;
    DECLARE v_discount INT DEFAULT 0;
    DECLARE v_base_amount DECIMAL(10,2);
    DECLARE v_discount_amount DECIMAL(10,2);
     /*
        Global error handler.
        If ANY SQL exception occurs after START TRANSACTION,
        this handler executes automatically.
        This guarantees atomicity.
    */
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET o_success = FALSE;
        SET o_message = 'Transaction failed due to database error';
    END;

    /*
        Always initialize OUT parameters.
        This avoids returning garbage values.
    */
    SET o_success = FALSE;
    SET o_message = '';
    SET o_registration_id = NULL;
    SET o_final_amount = 0;

    START TRANSACTION;

    /*
        Step 1: Lock the ticket row.
        FOR UPDATE ensures that concurrent registrations
        cannot overbook tickets.
    */
    SELECT available_quantity
    INTO v_available
    FROM tickets
    WHERE ticket_id = p_ticket_id
    FOR UPDATE;

    /*
        Step 2: Validate ticket availability.
        If insufficient, rollback and exit procedure early.
    */
    IF v_available < p_quantity THEN
        ROLLBACK;
        SET o_message = 'Insufficient tickets available';
        LEAVE proc_block;
    END IF;

    /*
        Step 3: Validate offer code only if provided.
        Blank or NULL offer codes are ignored.
    */
    IF p_offer_code IS NOT NULL AND TRIM(p_offer_code) <> '' THEN
        SELECT offer_id, discount_percentage
        INTO v_offer_id, v_discount
        FROM offers
        WHERE event_id = p_event_id
          AND UPPER(code) = UPPER(p_offer_code)
          AND (valid_from IS NULL OR valid_from <= NOW())
          AND (valid_to IS NULL OR valid_to >= NOW());

         /*
            If no offer row is found, offer_id remains NULL.
        */
        IF v_offer_id IS NULL THEN
            ROLLBACK;
            SET o_message = 'Invalid or expired offer code';
            LEAVE proc_block;
        END IF;

         /*
            Prevent the same user from using the same offer again.
        */
        IF EXISTS (
            SELECT 1
            FROM offer_usages
            WHERE offer_id = v_offer_id
              AND user_id = p_user_id
        ) THEN
            ROLLBACK;
            SET o_message = 'Offer code already used by user';
            LEAVE proc_block;
        END IF;
    END IF;
     /*
        Step 4: Create registration record.
        LAST_INSERT_ID is safe inside a transaction.
    */
     INSERT INTO registrations (
        user_id,
        event_id,
        registration_date,
        status
    )
    VALUES (
        p_user_id,
        p_event_id,
        UTC_TIMESTAMP(),
        'CONFIRMED'
    );

    SET o_registration_id = LAST_INSERT_ID();

    /*
        Step 5: Link tickets to registration.
    */
    INSERT INTO registration_tickets (
        registration_id,
        ticket_id,
        quantity
    )
    VALUES (
        o_registration_id,
        p_ticket_id,
        p_quantity
    );

    /*
        Step 6: Calculate payable amount.
        All monetary calculation happens in DB
        to avoid mismatch with Java.
    */
    SET v_base_amount = p_price * p_quantity;
    SET v_discount_amount = (v_base_amount * v_discount) / 100;
    SET o_final_amount = v_base_amount - v_discount_amount;

    /*
        Step 7: Record payment.
        If this insert fails, EXIT HANDLER rolls back everything.
    */
    INSERT INTO payments (
        registration_id,
        amount,
        payment_method,
        payment_status,
        created_at,
        offer_id
    )
    VALUES (
        o_registration_id,
        o_final_amount,
        p_payment_method,
        'SUCCESS',
        UTC_TIMESTAMP(),
        v_offer_id
    );

    /*
        Step 8: Deduct ticket quantity only after payment success.
    */
    UPDATE tickets
    SET available_quantity = available_quantity - p_quantity
    WHERE ticket_id = p_ticket_id;

    /*
        Step 9: Record offer usage only if offer was applied.
    */
    IF v_offer_id IS NOT NULL THEN
        INSERT INTO offer_usages (
            offer_id,
            user_id,
            registration_id,
            used_at
        )
        VALUES (
            v_offer_id,
            p_user_id,
            o_registration_id,
            UTC_TIMESTAMP()
        );
    END IF;

    /*
        Step 10: Commit transaction.
        At this point, all data is consistent.
    */
    COMMIT;

    SET o_success = TRUE;
    SET o_message = 'Registration successful';

END proc ;;
DELIMITER ;

-- Declare output variables
SET @success = FALSE;
SET @msg = '';
SET @reg_id = NULL;
SET @final_amt = 0;

-- Call the procedure
CALL sp_register_for_event(
    2,            -- user_id
    1,            -- event_id
    3,            -- ticket_id
    2,            -- quantity
    999.00,       -- price per ticket
    'CARD',       -- payment method
    'NEWCODE10', -- offer code
    @success,
    @msg,
    @reg_id,
    @final_amt
);

SELECT @success AS success, @msg AS message, @reg_id AS registration_id, @final_amt AS final_amount;



