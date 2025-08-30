package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // --- CHECK THESE DETAILS CAREFULLY ---
    private static final String URL = "jdbc:mysql://localhost:3306/bloodbank";
    private static final String USER = "root";
    
    // ✅ Replace this with your actual MySQL root password
    private static final String PASSWORD = "Nevin@2450560"; 
    
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            // This error happens if the MySQL Connector/J JAR is missing
            throw new SQLException("MySQL JDBC Driver not found. Please add the JAR to your project.", e);
        }
        
        // Attempt to establish the connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}