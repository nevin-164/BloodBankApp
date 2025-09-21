package servlet; // ⚠️ Make sure this package name matches yours!

import dao.StockDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap; // ✅ ADDED: We need this for our new status map
import java.sql.SQLException;

@WebServlet("/public-dashboard") // This annotation maps the URL
public class PublicDashboardServlet extends HttpServlet {

    // Define the levels for our bar graph. We can easily change these later.
    private static final int LEVEL_LOW = 10;
    private static final int LEVEL_MEDIUM = 40;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 1. Call our new DAO method to get the aggregate totals
            // This gets a map like {"O+": 50, "A+": 12, "B-": 3}
            Map<String, Integer> aggregateStock = StockDAO.getAggregateStock();
            
            // 2. Create a new map for the status levels (e.g., "O+" -> "High")
            Map<String, String> stockStatusMap = new HashMap<>();
            
            // 3. Loop through the real numbers and convert them to status strings
            for (Map.Entry<String, Integer> entry : aggregateStock.entrySet()) {
                String bloodGroup = entry.getKey();
                int units = entry.getValue();
                String status;
                
                if (units == 0) {
                    status = "Empty";
                } else if (units <= LEVEL_LOW) {
                    status = "Low";
                } else if (units <= LEVEL_MEDIUM) {
                    status = "Medium";
                } else {
                    status = "High";
                }
                stockStatusMap.put(bloodGroup, status);
            }
            
            // 4. Send the NEW map (the status map) to the JSP
            request.setAttribute("stockStatusMap", stockStatusMap);
            request.getRequestDispatcher("/public-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) { 
            e.printStackTrace();
            // 500 Internal Server Error
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching public stock data.");
        }
    }
}