/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
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
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

//        System.out.println(signIn);
        String email = signIn.get("email").getAsString();
        String password = signIn.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (email.isEmpty()) {

            responseObject.addProperty("message", "Email can't be Empty!");

        } else if (!Util.isEmailValid(email)) {

            responseObject.addProperty("message", "Please enter valid email!");

        } else if (password.isEmpty()) {

            responseObject.addProperty("message", "Password can't be Empty!");

        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));
            c.add(Restrictions.eq("password", password));

            User u = (User) c.uniqueResult(); //  fixed: get actual user

            if (u == null) {

                responseObject.addProperty("message", "Invalid credentials!");

            } else {

                responseObject.addProperty("status", true);
                HttpSession ses = request.getSession();

                if (u.getVerification() == null
                        || (!u.getVerification().equals("Verified") && !u.getVerification().equals("Admin"))) {

                    ses.setAttribute("email", email);
                    responseObject.addProperty("message", "Not"); // Not verified

                } else {

                    if (u.getVerification().equals("Verified")) {
                        ses.setAttribute("user", u);
                        responseObject.addProperty("message", "Verified"); // Verified User

                    } else if (u.getVerification().equals("Admin")) {
                        ses.setAttribute("user", u);
                        responseObject.addProperty("message", "Admin"); // Verified Admin
                    }
                }

            }

            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
