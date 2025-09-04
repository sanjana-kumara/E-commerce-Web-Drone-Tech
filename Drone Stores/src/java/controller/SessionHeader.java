/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "SessionHeader", urlPatterns = {"/SessionHeader"})
public class SessionHeader extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // session
        HttpSession session = request.getSession(false);
        String email = "support@Drontech.com";
        String headerHtml = "";

        if (session != null && session.getAttribute("user") != null) {
            User u = (User) session.getAttribute("user");

            if ("Verified".equalsIgnoreCase(u.getVerification())) {
                
                email = u.getEmail();

                headerHtml = "<div class=\"container\">"
                        + "<div class=\"row align-items-center\">"
                        + "<div class=\"col-lg-6 col-md-6\">"
                        + "<div class=\"support_info\">"
                        + "<p>Email: <a href=\"mailto:" + email + "\">" + email + "</a></p>"
                        + "</div>"
                        + "</div>"
                        + "<div class=\"col-lg-6 col-md-6\">"
                        + "<div class=\"top_right text-right\">"
                        + "<ul>"
                        + "<li><a href=\"my-account.html\">Account</a></li>"
                        + "<li><a href=\"checkout.html\">Checkout</a></li>"
                        + "<li><a href=\"productadd.html\">Become A Seller</a></li>"
                        + "</ul>"
                        + "</div>"
                        + "</div>"
                        + "</div>"
                        + "</div>";

            } else if ("Admin".equalsIgnoreCase(u.getVerification())) {
                
                email = u.getEmail();
                
                headerHtml = "<div class=\"container\">"
                        + "<div class=\"row align-items-center\">"
                        + "<div class=\"col-lg-6 col-md-6\">"
                        + "<div class=\"support_info\">"
                        + "<p>Email: <a href=\"mailto:" + email + "\">" + email + "</a></p>"
                        + "</div>"
                        + "</div>"
                        + "<div class=\"col-lg-6 col-md-6\">"
                        + "<div class=\"top_right text-right\">"
                        + "<ul>"
                        + "<li><a onclick=\"signOut();\">Log Out</a></li>"
                        + "</ul>"
                        + "</div>"
                        + "</div>"
                        + "</div>"
                        + "</div>";
            }
        } else {
            // Guest header
            headerHtml = "<div class=\"container\">"
                    + "<div class=\"row align-items-center\">"
                    + "<div class=\"col-lg-6 col-md-6\">"
                    + "<div class=\"support_info\">"
                    + "<p>Email: <a href=\"mailto:" + email + "\">" + email + "</a></p>"
                    + "</div>"
                    + "</div>"
                    + "<div class=\"col-lg-6 col-md-6\">"
                    + "<div class=\"top_right text-right\">"
                    + "<ul>"
                    + "<li><a href=\"signin.html\">Sign In</a></li>"
                    + "<li><a href=\"signup.html\">Sign Up</a></li>"
                    + "</ul>"
                    + "</div>"
                    + "</div>"
                    + "</div>"
                    + "</div>";
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(headerHtml);
        
    }
    
}
