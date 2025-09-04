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
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import sun.awt.windows.ThemeReader;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();
        String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(email);
        System.out.println(password);

        JsonObject responsObject = new JsonObject();

        responsObject.addProperty("status", false);

        if (firstName.isEmpty()) {

            responsObject.addProperty("message", "First Name can be empty!");

        } else if (lastName.isEmpty()) {

            responsObject.addProperty("message", "Last Name can be empty!");

        } else if (email.isEmpty()) {

            responsObject.addProperty("message", "Email can be empty!");

        } else if (!Util.isEmailValid(email)) {

            responsObject.addProperty("message", "Please enter a Valide email!");

        } else if (password.isEmpty()) {

            responsObject.addProperty("message", "Password can be empty!");

        } else if (!Util.isPasswordValid(password)) {

            responsObject.addProperty("message", "The Password must contains at least uppercase,lowercase "
                    + "number, special character and to be eight characters long!");

        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {

                responsObject.addProperty("message", "User with this email already exists!!");

            } else {

                User u = new User();

                u.setFirst_name(firstName);
                u.setLast_name(lastName);
                u.setEmail(email);
                u.setPassword(password);

                // genarate verification code
                final String verificationCode = Util.genrateCode();
                u.setVerification(verificationCode);

                u.setCreated_at(new Date());

                session.save(u);
                session.beginTransaction().commit();

                // send Email
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Mail.sendMail(email, "Dronetech - Email Verification",
                                "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9fafb; color: #333;'>"
                                + "<div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);'>"
                                + "<div style='text-align: center;'>"
                                + "<img src='https://raw.githubusercontent.com/WEB-II-Development/img-host/refs/heads/main/logo6.png' alt='Drontech Logo' style='width: 120px; margin-bottom: 20px;'/>"
                                + "<h2 style='color: #6b21a8;'>Dronetech</h2>"
                                + "</div>"
                                + "<p style='text-align: center; font-size: 18px;'>Welcome to Drontech! To complete your registration, please use the verification code below:</p>"
                                + "<div style='margin: 20px auto; width: fit-content; background-color: #f3f4f6; border-radius: 8px; padding: 15px 30px;'>"
                                + "<h1 style='margin: 0; font-size: 32px; color: #4f46e5; letter-spacing: 4px;'>" + verificationCode + "</h1>"
                                + "</div>"
                                + "<p style='text-align: center; font-size: 14px; color: #6b7280;'>If you did not request this, you can safely ignore this email.</p>"
                                + "<p style='text-align: center; font-size: 14px;'>Thank you, <br><strong>Drontech Team</strong></p>"
                                + "</div>"
                                + "</div>"
                        );

                    }
                }).start();

                //session management
                HttpSession ses = request.getSession();
                ses.setAttribute("email", email);
                //session management

                responsObject.addProperty("status", true);

                responsObject.addProperty("message", "Registration success!!. Please check You email for the verification code.");

            }

            session.close();

        }

        String responseText = gson.toJson(responsObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
