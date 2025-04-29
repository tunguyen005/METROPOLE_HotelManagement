-- MySQL dump 10.13  Distrib 9.1.0, for Win64 (x86_64)
--
-- Host: localhost    Database: Metropole
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','admin123');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `room_id` int DEFAULT NULL,
  `employee_id` int DEFAULT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `status` enum('BOOKED','CHECKED_IN','CHECKED_OUT','CANCELLED') DEFAULT 'BOOKED',
  `note` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  KEY `room_id` (`room_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `booking_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES (1,1,2,4,'2025-04-03','2025-04-08','CHECKED_IN','Check in lúc 12h00 trưa','2025-04-13 05:30:17'),(2,1,2,4,'2025-04-01','2025-04-03','CHECKED_OUT','Đến sớm','2025-04-14 16:42:52');
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `gender` enum('Male','Female','Other') NOT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `identity_number` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `nationality` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `address` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Trần Thị Thùy Trang','Female','0909420552','trangthuy@gmail.com','070204435503','Việt Nam','62 Lý Chính Thắng','2025-04-12 09:42:17','N/A'),(2,'Nguyen Van A','Male','0901234567','nva@example.com','123456789','Vietnam','Hanoi','2025-04-14 17:21:54',NULL),(3,'Tran Thi B','Female','0909876543','ttb@example.com','987654321','Vietnam','HCMC','2025-04-14 17:21:54',NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` enum('Reception','Security','Management','Accounting','Housekeeping','Driver') DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'Driver','Tài xế'),(2,'Reception','Phòng lễ tân'),(3,'Security','Phòng bảo vệ'),(4,'Management','Phòng quản lý'),(5,'Accounting','Phòng kế toán'),(6,'Housekeeping','Phòng dọn dẹp'),(7,'Reception','Phòng lễ tân');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driver`
--

DROP TABLE IF EXISTS `driver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `driver` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `age` int NOT NULL,
  `gender` enum('Male','Female','Others') NOT NULL,
  `company` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `car_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `car_type` enum('4-Seater','7-Seater','Limousine','Bus','Other') DEFAULT '4-Seater',
  `license_plate` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `available` enum('Yes','No') DEFAULT 'Yes',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `note` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `license_plate` (`license_plate`),
  KEY `idx_driver_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `driver`
--

LOCK TABLES `driver` WRITE;
/*!40000 ALTER TABLE `driver` DISABLE KEYS */;
INSERT INTO `driver` VALUES (3,'Lâm Văn Bền',34,'Male','NinjaVan','Suzuki 53','7-Seater','50K33333','Vòng xoay Điện Biên Phủ','Yes','0909943444','C:\\Users\\nguye\\Downloads\\vanben.jpg',NULL);
/*!40000 ALTER TABLE `driver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `age` varchar(3) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `gender` enum('Male','Female','Others') NOT NULL,
  `job` enum('Receptionist','Security','Manager','Accountant','Cleaner','Driver') NOT NULL,
  `salary` float NOT NULL,
  `phone` varchar(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` enum('Active','Inactive','On Leave') DEFAULT 'Active',
  `department_id` int DEFAULT NULL,
  `shift` enum('Morning','Afternoon','Night') DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `department_id` (`department_id`),
  KEY `idx_employee_phone` (`phone`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (2,'Lâm Văn Bền','34','Male','Driver',20000000,'0909943444','N/A','Vòng xoay Điện Biên Phủ','2025-04-12','Active',1,'Morning','C:\\Users\\nguye\\Downloads\\vanben.jpg'),(3,'Trần Thanh Tùng','18','Male','Cleaner',20000000,'0909043332','tungthanh2811@gmail.com','Trần Quốc Toản, Q3','2025-04-12','Active',6,'Night','C:\\Users\\nguye\\Downloads\\thanhtung.jpg'),(4,'Lý Tuân','24','Male','Receptionist',25000000,'0976534315','tuanly@gmail.com','Lý Thường Kiệt, Q5','2025-04-12','Active',2,'Afternoon','C:\\Users\\nguye\\Downloads\\lytuan.jpg'),(5,'Nguyễn Trần Minh Tú','21','Male','Manager',50000000,'0904344385','tunguyen@gmail.com','Lý Chính Thắng, Q3','2025-04-12','Inactive',4,'Morning','C:\\Users\\nguye\\Downloads\\minhtu.jpg'),(6,'Nguyễn Lê Minh Thư','21','Female','Manager',50000000,'0943348852','minhna@gmail.com','Ngô Tất Tố, Q. Bình Thạnh','2025-04-12','Active',4,'Afternoon','C:\\Users\\nguye\\Downloads\\minhna.jpeg'),(7,'Lê Thanh Tuấn','22','Male','Receptionist',50000000,'0905599533','N/A','Lý Chính Thắng','2025-04-14','Active',2,'Morning','C:\\Users\\nguye\\Downloads\\vanben.jpg'),(8,'Le Van C','30','Male','Receptionist',10000000,'0912345678','lvc@example.com','Hanoi','2024-01-01','Active',1,'Morning',NULL);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int DEFAULT NULL,
  `total_amount` float NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_method` enum('CASH','CARD','BANK_TRANSFER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `booking_id` (`booking_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,1,1500000,'2025-04-14 17:22:39','CASH'),(2,2,2000000,'2025-04-14 17:22:39','CARD');
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `invoice_id` int NOT NULL,
  `amount_paid` float NOT NULL,
  `payment_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `payment_method` enum('CASH','CARD','BANK_TRANSFER','MOMO','ZALO_PAY','OTHER') DEFAULT NULL,
  `status` enum('PAID','PARTIAL','PENDING') DEFAULT 'PAID',
  `note` text,
  PRIMARY KEY (`id`),
  KEY `invoice_id` (`invoice_id`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,500000,'2025-04-15 00:22:42','CASH','PARTIAL',NULL),(2,2,2000000,'2025-04-15 00:22:42','CARD','PAID',NULL),(3,1,1000000,'2025-04-15 00:23:09','CASH','PAID',NULL);
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` int NOT NULL AUTO_INCREMENT,
  `floor` int NOT NULL,
  `room_num` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `room_type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('AVAILABLE','OCCUPIED','MAINTENANCE') DEFAULT 'AVAILABLE',
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `bed_size` enum('SINGLE','DOUBLE','QUEEN','KING','TWIN') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `room_num` (`room_num`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (2,1,'101','Tiêu chuẩn','AVAILABLE','Phòng có phong cách trẻ trung, tươi mát.','2025-04-10 12:56:25','2025-04-10 12:56:25','QUEEN'),(3,1,'102','Suite','AVAILABLE','Phòng có gam màu vàng chủ đạo','2025-04-10 15:13:48','2025-04-10 15:13:48','DOUBLE'),(4,2,'201','Tiêu chuẩn','AVAILABLE','Phòng có phong cách châu Âu thời kỳ Phục Hưng','2025-04-10 16:29:49','2025-04-10 16:29:49','DOUBLE'),(5,2,'202','Tổng thống','MAINTENANCE','Phòng nghỉ tổng thống tráng lệ, nguy nga','2025-04-10 16:31:11','2025-04-10 16:31:11','KING'),(6,3,'301','Duplex','OCCUPIED','Căn hộ Duplex sang trọng','2025-04-10 16:31:49','2025-04-10 16:31:49','DOUBLE'),(7,1,'103','Tiêu chuẩn','OCCUPIED','Phòng nghỉ theo giờ','2025-04-11 03:26:57','2025-04-11 03:26:57','DOUBLE'),(11,1,'106','Standard','OCCUPIED',NULL,'2025-04-14 17:22:09','2025-04-14 17:22:09','SINGLE'),(12,1,'107','Deluxe','OCCUPIED',NULL,'2025-04-14 17:22:09','2025-04-14 17:22:09','DOUBLE');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `description` text,
  `price` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (1,'Buffet buổi sáng','Thực đơn 200 món buổi sáng Âu - Á cho quý khách đang nghỉ dưỡng tại khách sạn',2000000),(2,'Phòng xông hơi','Phòng xông hơi theo tiêu chuẩn Nhật Bản với đá nóng được lấy dưới chân núi lửa Fuji',1000000),(3,'Hồ bơi vô cực','Hồ bơi vô cực trên tầng 50 của khách sạn',500000),(4,'Bữa sáng tại hồ bơi riêng','Bữa sáng 10 món cho quý khách tại giường',1200000),(5,'Phòng massage thư giãn','Massage thư giãn cho quý khách bởi các chuyên gia massage có trên 5 năm kinh nghiệm và đặc biệt có kiến thức y khoa',1200000),(6,'Thuê xe hơi','Khách hàng có thể tự thuê xe theo giờ để đảm bảo tính riêng tư',1000000);
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_usage`
--

DROP TABLE IF EXISTS `service_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_usage` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int DEFAULT NULL,
  `service_id` int DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `usage_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `booking_id` (`booking_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `service_usage_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`),
  CONSTRAINT `service_usage_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_usage`
--

LOCK TABLES `service_usage` WRITE;
/*!40000 ALTER TABLE `service_usage` DISABLE KEYS */;
INSERT INTO `service_usage` VALUES (1,1,1,1,'2025-04-13 12:30:36');
/*!40000 ALTER TABLE `service_usage` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-15  0:34:11
