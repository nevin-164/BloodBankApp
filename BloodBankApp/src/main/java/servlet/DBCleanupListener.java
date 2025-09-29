package servlet;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebListener
public class DBCleanupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // This method is called when the application starts up.
        // We don't need to do anything here, but the method must be present.
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // This method is called when the application is shutting down.
        
        // 1. Get all the currently loaded JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                // 2. Deregister the MySQL driver
                if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                    DriverManager.deregisterDriver(driver);
                }
            } catch (SQLException e) {
                // Log an error message
                sce.getServletContext().log("Error deregistering JDBC driver", e);
            }
        }

        // 3. Stop the abandoned connection cleanup thread
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            sce.getServletContext().log("Error shutting down MySQL connection cleanup thread", e);
        }
    }
}