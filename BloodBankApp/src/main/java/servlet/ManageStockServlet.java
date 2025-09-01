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

                 switch (action) {
                    case "add":
                        StockDAO.addUnits(hospitalId, bloodGroup, units);
                        successMessage = "Successfully added " + units + " units of " + bloodGroup;
                        break;
                    case "remove":
                        // ✅ FIXED: Changed the method call from removeStock() to takeUnits()
                        StockDAO.takeUnits(hospitalId, bloodGroup, units);
                        successMessage = "Successfully removed " + units + " units of " + bloodGroup;
                        break;
                    case "set":
                        StockDAO.setStock(hospitalId, bloodGroup, units);
                        successMessage = "Stock for " + bloodGroup + " set to " + units + " units";
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

        String redirectURL = request.getContextPath() + "/hospital-dashboard.jsp";
        if (successMessage != null && !successMessage.isEmpty()) {
            redirectURL += "?success=" + successMessage.replace(" ", "+");
        } else if (errorMessage != null && !errorMessage.isEmpty()) {
            redirectURL += "?error=" + errorMessage.replace(" ", "+");
        }
        
        response.sendRedirect(redirectURL);
    }
}