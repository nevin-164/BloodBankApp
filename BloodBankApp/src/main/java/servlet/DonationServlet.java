package servlet;

import dao.DonationDAO;
import dao.HospitalDAO;
import dao.StockDAO;
import dao.UserDAO;
import model.Hospital;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/donate")
public class DonationServlet extends HttpServlet {
    private static final int SHELF_DAYS = 42;
    private static final int COOLING_DAYS = 90;

    // This method shows the donation form and populates the hospital list
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            List<Hospital> hospitals = HospitalDAO.getAllHospitals();
            req.setAttribute("hospitals", hospitals);
            req.getRequestDispatcher("donor.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error loading donation page data", e);
        }
    }

    // This method processes the donation
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // ✅ SECURED: Get the logged-in user from the session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null || !"DONOR".equals(((User) session.getAttribute("user")).getRole())) {
            res.sendRedirect("login.jsp?error=Please+login+as+a+donor+to+donate.");
            return;
        }
        User donor = (User) session.getAttribute("user");

        try {
            // Get form parameters
            String unitsStr = req.getParameter("units");
            String hospitalIdStr = req.getParameter("hospital_id");

            if (unitsStr == null || hospitalIdStr == null) {
                req.setAttribute("msg", "Missing parameters.");
                doGet(req, res);
                return;
            }

            int units = Integer.parseInt(unitsStr);
            int hospitalId = Integer.parseInt(hospitalIdStr);
            
            // Your existing logic for checking eligibility
            LocalDate today = LocalDate.now();
            Date dbNext = UserDAO.getNextEligibleDate(donor.getId());
            if (dbNext != null && today.isBefore(dbNext.toLocalDate())) {
                req.setAttribute("msg", "Not eligible yet. Next eligible on: " + dbNext.toString());
                doGet(req, res);
                return;
            }

            // Calculate dates
            Date nextEligible = Date.valueOf(today.plusDays(COOLING_DAYS));
            Date expiry = Date.valueOf(today.plusDays(SHELF_DAYS));

            // Insert donation using the SECURE donor ID and their blood group
            DonationDAO.insert(donor.getId(), donor.getBloodGroup(), Date.valueOf(today), expiry, units, hospitalId);

            // Update donor eligibility and stock
            UserDAO.updateDonationDates(donor.getId(), Date.valueOf(today), nextEligible);
            StockDAO.addUnits(donor.getBloodGroup(), units);

            req.setAttribute("msg", "Donation recorded successfully! Thank you for your contribution. 🩸");
            doGet(req, res); // Forward back to the donor page to show the success message

        } catch (NumberFormatException e) {
            req.setAttribute("msg", "Invalid number format for units.");
            doGet(req, res);
        } catch (Exception e) {
            throw new ServletException("Error processing donation", e);
        }
    }
}
