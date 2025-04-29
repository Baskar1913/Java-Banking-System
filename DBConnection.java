package BankSystem;

import java.sql.*;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banksystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Baskar1913!"; // Replace with your MySQL password
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Successfully connected to MySQL database!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Connection failed! Check your MySQL credentials");
            e.printStackTrace();
        }
        return conn;
    }

    // Test the connection (optional)
    public static void main(String[] args) {
        Connection testConn = getConnection();
        if (testConn != null) {
            try {
                testConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}