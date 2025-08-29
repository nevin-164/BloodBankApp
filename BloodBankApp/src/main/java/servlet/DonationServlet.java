package servlet;

import dao.UserDAO;
import dao.DonationDAO;
import dao.StockDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet("/donate")
public class DonationServlet extends HttpServlet {
    private static final int SHELF_DAYS = 42;
    private static final int COOLING_DAYS = 90;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String userIdStr = req.getParameter("user_id");
            String bg = req.getParameter("blood_group");
            String unitsStr = req.getParameter("units");

            if (userIdStr == null || bg == null || unitsStr == null) {
                req.setAttribute("msg", "Missing parameters.");
                req.getRequestDispatcher("donor.jsp").forward(req, res);
                return;
            }

            int userId = Integer.parseInt(userIdStr);
            int units = Integer.parseInt(unitsStr);

            if (units <= 0) {
                req.setAttribute("msg", "Units must be positive.");
                req.getRequestDispatcher("donor.jsp").forward(req, res);
                return;
            }

            LocalDate today = LocalDate.now();
            Date nextEligible = Date.valueOf(today.plusDays(COOLING_DAYS));
            Date expiry = Date.valueOf(today.plusDays(SHELF_DAYS));

            Date dbNext = UserDAO.getNextEligibleDate(userId);
            if (dbNext != null && today.isBefore(dbNext.toLocalDate())) {
                req.setAttribute("msg", "Not eligible yet. Next eligible on: " + dbNext.toString());
                req.getRequestDispatcher("donor.jsp").forward(req, res);
                return;
            }

            DonationDAO.insert(userId, bg, Date.valueOf(today), expiry, units);
            UserDAO.updateDonationDates(userId, Date.valueOf(today), nextEligible);
            StockDAO.addUnits(bg, units);

            req.setAttribute("msg", "Donation recorded! Thank you 🩸");
            req.getRequestDispatcher("donor.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            req.setAttribute("msg", "Invalid number format.");
            req.getRequestDispatcher("donor.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
