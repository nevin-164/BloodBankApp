package servlet;

import dao.HospitalDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/hospitals/edit")
public class EditHospitalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Load hospital data for editing
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("hospitalId"));
            Hospital hospital = HospitalDAO.getHospitalById(id);

            if (hospital == null) {
                res.sendRedirect(req.getContextPath() + "/admin/hospitals?error=Hospital not found");
                return;
            }

            req.setAttribute("hospital", hospital);
            req.getRequestDispatcher("/WEB-INF/editHospital.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error loading hospital for edit", e);
        }
    }

    // Process hospital update
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("hospitalId"));
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String contact = req.getParameter("contactNumber"); // match form input name
            String address = req.getParameter("address");

            Hospital hospital = new Hospital();
            hospital.setHospitalId(id);
            hospital.setName(name);
            hospital.setEmail(email);
            hospital.setContactNumber(contact);
            hospital.setAddress(address);

            HospitalDAO.updateHospital(hospital);

            // Redirect to hospital list with success message
            res.sendRedirect(req.getContextPath() + "/admin/hospitals?success=Hospital updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(req.getContextPath() + "/admin/hospitals?error=Unable to update hospital");
        }
    }
}
