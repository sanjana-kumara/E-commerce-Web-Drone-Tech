/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "ToggleUserStatus", urlPatterns = {"/ToggleUserStatus"})
public class ToggleUserStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId"));
        String action = request.getParameter("action"); // "Block" or "Active"

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        JsonObject result = new JsonObject();

        try {

            User user = (User) session.get(User.class, userId);

            if (user != null) {

                if ("Block".equalsIgnoreCase(action)) {

                    String randomCode = String.valueOf((int) (Math.random() * 900000) + 100000);
                    user.setVerification(randomCode);

                    result.addProperty("newStatus", "Deactivated");
                    result.addProperty("verification", randomCode);

                } else if ("Active".equalsIgnoreCase(action)) {

                    user.setVerification("Verified");
                    result.addProperty("newStatus", "Active");
                    result.addProperty("verification", "Verified");

                }

                session.update(user);
                tx.commit();

                result.addProperty("success", true);

            } else {

                result.addProperty("success", false);
                result.addProperty("message", "User not found");

            }

        } catch (Exception e) {

            if (tx != null) {

                tx.rollback();

            }

            result.addProperty("success", false);

            result.addProperty("message", e.getMessage());

        } finally {

            session.close();

        }

        response.setContentType("application/json");

        response.getWriter().write(result.toString());

    }

}
