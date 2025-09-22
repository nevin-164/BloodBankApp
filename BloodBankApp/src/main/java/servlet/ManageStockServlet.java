package servlet;

import dao.StockDAO;
import model.Hospital; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder; // ✅ ADDED

@WebServlet("/manage-stock")
public class ManageStockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect(request.getContextPath() + "/hospital-login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String bloodGroup = request.getParameter("bloodGroup");
        String successMessage = "";
        String errorMessage = "";

        try {
            int units = Integer.parseInt(request.getParameter("units"));
            if (units <= 0) {
                 errorMessage = "Units must be a positive number.";
            } else {
                 int hospitalId = hospital.getId();

                 // ✅ MODIFIED: This switch now WORKS with our new hybrid DAO
                 switch (action) {
                    case "add":
                        StockDAO.addUnits(hospitalId, bloodGroup, units);
                        successMessage = "Successfully added " + units + " manual units of " + bloodGroup;
                        break;
                    case "remove":
                        StockDAO.takeUnits(hospitalId, bloodGroup, units);
                        successMessage = "Successfully removed " + units + " manual units of " + bloodGroup;
                        break;
                    case "set":
                        StockDAO.setStock(hospitalId, bloodGroup, units);
                        successMessage = "Manual stock for " + bloodGroup + " set to " + units + " units";
                        break;
                    default:
                        errorMessage = "Invalid action specified.";
                        break;
                }
            }
        } catch (NumberFormatException e) {
            errorMessage = "Please enter a valid number for units.";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "An error occurred while updating the stock.";
        }

        // This redirect to the servlet is correct.
        String redirectURL = request.getContextPath() + "/hospital-dashboard";
        
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        }
        
        response.sendRedirect(redirectURL);
    }
}