package servlet;

import dao.UserDAO;
import dao.DonationDAO;
import dao.StockDAO;
import dao.HospitalDAO;  // New DAO for hospitals
import model.User;
import model.Hospital;  // ✅ Import Hospital

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/donate")
public class DonationServlet extends HttpServlet {
    private static final int SHELF_DAYS = 42;
    private static final int COOLING_DAYS = 90;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            // ✅ Get list of hospitals for dropdown
            List<Hospital> hospitals = HospitalDAO.getAllHospitals();
            req.setAttribute("hospitals", hospitals);
            req.getRequestDispatcher("donor.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String userIdStr = req.getParameter("user_id");
            String bg = req.getParameter("blood_group");
            String unitsStr = req.getParameter("units");
            String hospitalIdStr = req.getParameter("hospital_id");  // Selected hospital

            if (userIdStr == null || bg == null || unitsStr == null || hospitalIdStr == null) {
                req.setAttribute("msg", "Missing parameters.");
                doGet(req, res);  // Show form with hospital dropdown
                return;
            }

            int userId = Integer.parseInt(userIdStr);
            int units = Integer.parseInt(unitsStr);
            int hospitalId = Integer.parseInt(hospitalIdStr);

            if (units <= 0) {
                req.setAttribute("msg", "Units must be positive.");
                doGet(req, res);
                return;
            }

            LocalDate today = LocalDate.now();
            Date nextEligible = Date.valueOf(today.plusDays(COOLING_DAYS));
            Date expiry = Date.valueOf(today.plusDays(SHELF_DAYS));

            Date dbNext = UserDAO.getNextEligibleDate(userId);
            if (dbNext != null && today.isBefore(dbNext.toLocalDate())) {
                req.setAttribute("msg", "Not eligible yet. Next eligible on: " + dbNext.toString());
                doGet(req, res);
                return;
            }

            // Insert donation with hospital
            DonationDAO.insert(userId, bg, Date.valueOf(today), expiry, units, hospitalId);

            // Update donor eligibility and stock
            UserDAO.updateDonationDates(userId, Date.valueOf(today), nextEligible);
            StockDAO.addUnits(bg, units);

            req.setAttribute("msg", "Donation recorded! Thank you 🩸");
            doGet(req, res);

        } catch (NumberFormatException e) {
            req.setAttribute("msg", "Invalid number format.");
            doGet(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
