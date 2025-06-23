# Java-Banking-System
Java Swing Desktop Banking System with JDBC and MySQL Integration

# 💻 Java Desktop Banking System

A **Java-based desktop banking application** built with **Swing GUI**, **JDBC**, and **MySQL**. This project enables bank employees to manage customer data, perform account transactions, and view real-time transaction history — all from a secure desktop interface.

---

## 🔑 Developed By
**Baskar M**

---

## 🚀 Key Features

- 🔐 Employee login system with last login tracking
- 👤 Customer account creation, update, and deletion
- 💰 Deposit and withdrawal transactions with descriptions
- 📊 Balance enquiry and live account status
- 📜 Transaction history with timestamps and IDs
- 📝 Closure tracking with reason and final balance
- ✅ Validations for Aadhar, PAN, and unique identifiers

---

## 🛠️ Tech Stack

| Layer        | Technology       |
|--------------|------------------|
| Language     | Java             |
| GUI          | Java Swing       |
| Database     | MySQL            |
| Connectivity | JDBC             |
| Layout       | CardLayout + Panels |
| IDE          | Eclipse          |

---

## 🗃️ Database: `banksystem`

### 📁 Tables

#### 🔹 `customers`
Stores customer personal and account details.

| Field                | Type                          | Key  | Description                      |
|---------------------|-------------------------------|------|----------------------------------|
| `account_number`     | BIGINT                        | PK   | Auto-incremented primary key     |
| `first_name`         | VARCHAR(50)                   |      | Customer's first name            |
| `last_name`          | VARCHAR(50)                   |      | Customer's last name             |
| `dob`                | DATE                          |      | Date of birth                    |
| `gender`             | ENUM('Male','Female','Other') |      | Gender                           |
| `address`            | TEXT                          |      | Residential address              |
| `city`               | VARCHAR(50)                   |      | City                             |
| `state`              | VARCHAR(50)                   |      | State                            |
| `pin_code`           | VARCHAR(10)                   |      | Postal code                      |
| `mobile`             | VARCHAR(15)                   |      | Mobile number                    |
| `email`              | VARCHAR(100)                  | YES  | Email (nullable)                 |
| `aadhar_number`      | VARCHAR(20)                   | UNI  | Unique Aadhar number             |
| `pan_number`         | VARCHAR(20)                   | UNI  | Unique PAN number                |
| `account_type`       | ENUM('Savings','Current')     |      | Type of account                  |
| `balance`            | DECIMAL(15,2)                 |      | Current account balance          |
| `account_created_date` | DATETIME                   |      | Account creation timestamp       |
| `account_status`     | ENUM('Active','Closed')       |      | Status of the account            |

---

#### 🔹 `employees`
Bank staff authentication and info.

| Field        | Type          | Key  | Description                      |
|--------------|---------------|------|----------------------------------|
| `employee_id`| INT           | PK   | Auto-incremented ID              |
| `username`   | VARCHAR(50)   | UNI  | Unique login username            |
| `password`   | VARCHAR(100)  |      | Hashed password                  |
| `full_name`  | VARCHAR(100)  |      | Full name                        |
| `email`      | VARCHAR(100)  | UNI  | Unique email                     |
| `last_login` | DATETIME      |      | Last login timestamp             |

---

#### 🔹 `transactions`
Tracks all customer transactions.

| Field              | Type                                    | Key  | Description                      |
|-------------------|-----------------------------------------|------|----------------------------------|
| `transaction_id`   | VARCHAR(20)                             | PK   | Unique transaction ID            |
| `account_number`   | BIGINT                                  | FK   | Associated customer account      |
| `transaction_type` | ENUM('Deposit','Withdrawal','Transfer') |      | Type of transaction              |
| `amount`           | DECIMAL(15,2)                           |      | Transaction amount               |
| `transaction_date` | DATETIME                                |      | Transaction timestamp            |
| `description`      | VARCHAR(255)                            | YES  | Additional info (nullable)       |

---

#### 🔹 `account_closures`
Logs details of closed customer accounts.

| Field            | Type          | Key  | Description                      |
|------------------|---------------|------|----------------------------------|
| `closure_id`     | INT           | PK   | Auto-incremented ID              |
| `account_number` | BIGINT        | FK   | Account being closed             |
| `closure_date`   | DATETIME      |      | Timestamp of closure             |
| `reason`         | TEXT          |      | Reason for closing account       |
| `final_balance`  | DECIMAL(15,2) |      | Returned balance to customer     |
| `handled_by`     | INT           | FK   | Employee who processed closure   |

---

## 🧪 How to Run This Project in Eclipse

1. **Open Eclipse**
   - Go to **File > Import > Existing Projects into Workspace**
   - Select the project folder.

2. **Add MySQL JDBC JAR to Build Path**
   - Right-click your project → Build Path → Configure Build Path
   - Go to **Libraries** → Click **Add External JARs...**
   - Choose: `lib/mysql-connector-java-8.0.xx.jar`

3. **Setup MySQL Database**
   - Run the `schema.sql` (or manually create the schema/tables)
   - Make sure the `DBConnection.java` file has correct DB credentials:
     ```java
     Connection con = DriverManager.getConnection(
         "jdbc:mysql://localhost:3306/banksystem", "root", "yourpassword");
     ```

4. **Run the Main Class**
   - Right-click `BankAppMain.java` or your starting UI class → Run As → Java Application

---

## 📦 How to Use the JAR File

### ✅ Run JAR from Terminal (outside Eclipse)

```bash
cd dist
java -jar BankApp.jar
