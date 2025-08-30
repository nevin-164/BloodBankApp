package servlet;

import dao.UserDAO;
import model.User;
import dao.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/admin/deleteUser")
public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("userId"));
        try {
            // 1️⃣ Delete all dependent requests first
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM requests WHERE patient_id=?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            // 2️⃣ Delete the user
            UserDAO.deleteUser(userId);

            // 3️⃣ Redirect to user list
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
