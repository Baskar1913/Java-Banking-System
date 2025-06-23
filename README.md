# 💻 Java Swing Desktop Banking System

A full-featured **Java desktop banking application** developed using **Java Swing**, **JDBC**, and **MySQL**. This system is designed for bank employees to manage customer accounts, perform transactions, and maintain real-time records via a modern, interactive interface.

---

## 👨‍💼 Developed By

**Baskar M**

---

## 🚀 Key Features

* 🔐 Employee login system with last login tracking
* 👤 Customer account creation, update, and deletion
* 💰 Deposit and withdrawal transactions with descriptions
* 📈 Balance enquiry and live account status
* 📜 Transaction history with timestamps and IDs
* 📌 Closure tracking with reason and final balance
* ✅ Validations for Aadhar, PAN, and unique identifiers

---

## 🛠️ Tech Stack

| Layer        | Technology           |
| ------------ | -------------------- |
| Language     | Java                 |
| GUI          | Java Swing           |
| Database     | MySQL                |
| Connectivity | JDBC                 |
| Layout       | CardLayout + JPanels |
| IDE          | Eclipse              |

---

## 📁 Project Structure

```
BankApp/
🔹 src/
🔹🔹 BankSystem/
      🔹 BankEmployeeSystem.java
      🔹 DBConnection.java
🔹 lib/
      🔹 mysql-connector-j-9.3.0.jar
```

---

## 🗓️ MySQL Database: `banksystem`

### 🔹 Table: `customers`

| Field                           | Type                          |
| ------------------------------- | ----------------------------- |
| account\_number                 | BIGINT (PK, auto-increment)   |
| first\_name                     | VARCHAR(50)                   |
| last\_name                      | VARCHAR(50)                   |
| dob                             | DATE                          |
| gender                          | ENUM('Male','Female','Other') |
| address, city, state, pin\_code | Various                       |
| mobile, email                   | VARCHAR                       |
| aadhar\_number                  | VARCHAR(20) UNIQUE            |
| pan\_number                     | VARCHAR(20) UNIQUE            |
| account\_type                   | ENUM('Savings','Current')     |
| balance                         | DECIMAL(15,2)                 |
| account\_status                 | ENUM('Active','Closed')       |
| account\_created\_date          | DATETIME                      |

### 🔹 Table: `employees`

| Field        | Type                |
| ------------ | ------------------- |
| employee\_id | INT (PK)            |
| username     | VARCHAR(50) UNIQUE  |
| password     | VARCHAR(100)        |
| full\_name   | VARCHAR(100)        |
| email        | VARCHAR(100) UNIQUE |
| last\_login  | DATETIME            |

### 🔹 Table: `transactions`

| Field             | Type                                    |
| ----------------- | --------------------------------------- |
| transaction\_id   | VARCHAR(20) (PK)                        |
| account\_number   | BIGINT (FK)                             |
| transaction\_type | ENUM('Deposit','Withdrawal','Transfer') |
| amount            | DECIMAL(15,2)                           |
| transaction\_date | DATETIME                                |
| description       | VARCHAR(255) (nullable)                 |

### 🔹 Table: `account_closures`

| Field           | Type          |
| --------------- | ------------- |
| closure\_id     | INT (PK)      |
| account\_number | BIGINT (FK)   |
| closure\_date   | DATETIME      |
| reason          | TEXT          |
| final\_balance  | DECIMAL(15,2) |
| handled\_by     | INT (FK)      |

---

## 🧪 How to Run This Project in Eclipse

This section explains how to set up and run the project within Eclipse.

---

### ✅ Project Structure

Ensure your project looks like this in Eclipse:

```
BankApp/
🔹 src/
🔹🔹 BankSystem/
      🔹 BankEmployeeSystem.java
      🔹 DBConnection.java
🔹 lib/
      🔹 mysql-connector-j-9.3.0.jar
```

* `src` contains your Java packages and source code
* `lib` contains the MySQL JDBC driver `.jar`
* The JAR appears under **Referenced Libraries** in Eclipse

---

### 🔧 Step 1: Import the Project

1. Open Eclipse
2. Go to **File > Import > Existing Projects into Workspace**
3. Select the folder that contains `BankApp`
4. Click **Finish**

---

### 🧬 Step 2: Add MySQL JDBC JAR to Build Path

1. Right-click on the project (`BankApp`) → **Build Path > Configure Build Path**
2. Go to the **Libraries** tab
3. Click **Add JARs...**
4. Select:

   ```
   lib/mysql-connector-j-9.3.0.jar
   ```
5. Click **Apply and Close**

✅ Your project is now linked with the MySQL JDBC driver.

---

### 📚 Step 3: Download MySQL Connector JAR (If Not Present)

If the JDBC driver JAR (`mysql-connector-j-9.3.0.jar`) is not in your `lib/` folder:

1. Go to the official MySQL Connector/J download page:
   👉 [https://dev.mysql.com/downloads/connector/j/](https://dev.mysql.com/downloads/connector/j/)

2. Choose the platform-independent `.zip` or `.tar.gz` version

3. Extract the download

4. Copy the `.jar` file (e.g., `mysql-connector-j-9.3.0.jar`) into your project’s `lib/` folder

5. Repeat **Step 2** to add it to Eclipse build path

✅ Now your project can connect to MySQL using JDBC.

---

### 🗃️ Step 4: Set Up the MySQL Database

1. Open your MySQL client (e.g., MySQL Workbench or terminal)

2. Create the database:

   ```sql
   CREATE DATABASE banksystem;
   ```

3. Create the tables manually or import them using a provided `schema.sql` file

4. Verify your DB connection code in `DBConnection.java`:

   ```java
   Connection con = DriverManager.getConnection(
       "jdbc:mysql://localhost:3306/banksystem", "root", "yourpassword");
   ```

---

### ▶️ Step 5: Run the Application

1. Open:

   ```
   src > BankSystem > BankEmployeeSystem.java
   ```

2. Right-click → **Run As > Java Application**

✅ The banking system UI will launch, and it will connect to the MySQL database.

---
