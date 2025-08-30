package servlet;

import dao.HospitalDAO;
import model.Hospital;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/AddHospitalServlet")
public class AddHospitalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contactNumber = request.getParameter("contactNumber");
        String address = request.getParameter("address");

        Hospital hospital = new Hospital();
        hospital.setName(name);
        hospital.setEmail(email);
        hospital.setPassword(password);
        hospital.setContactNumber(contactNumber);
        hospital.setAddress(address);

        try {
            // Correct DAO method
            HospitalDAO.insertHospital(hospital);

            response.sendRedirect("adminHospitals.jsp?success=Hospital added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("adminHospitals.jsp?error=Unable to add hospital");
        }
    }
}
