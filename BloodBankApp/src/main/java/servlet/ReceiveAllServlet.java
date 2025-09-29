package servlet;

import dao.BloodInventoryDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles the "Receive All" button click.
 * This servlet performs a bulk update to receive all in--transit shipments
 * for the currently logged-in hospital.
 */
@WebServlet("/receive-all")
public class ReceiveAllServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Hospital hospital = (session != null) ? (Hospital) session.getAttribute("hospital") : null;

        if (hospital == null) {
            response.sendRedirect("hospital-login.jsp");
            return;
        }

        try {
            int hospitalId = hospital.getId();
            // Call the new DAO method to perform the bulk update
            int bagsReceived = BloodInventoryDAO.receiveAllBagsForHospital(hospitalId);

            if (bagsReceived > 0) {
                session.setAttribute("successMessage", "Successfully received " + bagsReceived + " bag(s) into inventory.");
            } else {
                session.setAttribute("errorMessage", "There were no pending shipments to receive.");
            }
            
            response.sendRedirect("hospital-dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred while receiving all bags: " + e.getMessage());
            response.sendRedirect("hospital-dashboard");
        }
    }
}
