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
@WebServlet(name = "SessionHeader3", urlPatterns = {"/SessionHeader3"})
public class SessionHeader3 extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String headerHtml = "";

        if (session != null && session.getAttribute("user") != null) {

            User u = (User) session.getAttribute("user");

            String role = u.getVerification();

            if ("Admin".equalsIgnoreCase(role)) {

                headerHtml = "<div class=\"container\">\n"
                        + "<div class=\"row justify-content-center\">\n"
                        + "<div class=\"col-lg-12 col-md-12\">\n"
                        + "<div class=\"main_menu menu_position\">\n"
                        + "<nav>\n"
                        + "<ul>\n"
                        + "<li><a href=\"admin-user-management.html\">User Management</a></li>\n"
                        + "<li><a href=\"admin-product-management.html\">Product Management</a></li>\n"
                        + "<li><a href=\"admin-order-management.html\">Order Management</a></li>\n"
                        + "<li><a href=\"admin-dashboard.html\">Product Report</a></li>\n"
                        + "</ul>\n"
                        + "</nav>\n"
                        + "</div></div></div></div>";

            } else if ("Verified".equalsIgnoreCase(role)) {

                headerHtml = "<div class=\"container\">\n"
                        + "<div class=\"row justify-content-center\">\n"
                        + "<div class=\"col-lg-12 col-md-12\">\n"
                        + "<div class=\"main_menu menu_position\">\n"
                        + "<nav>\n"
                        + "<ul>\n"
                        + "<li><a href=\"index.html\">home</a></li>\n"
                        + "<li><a href=\"productview.html\">Product</a></li>\n"
                        + "<li><a href=\"checkout.html\">checkout</a></li>\n"
                        + "<li><a href=\"cart.html\">cart</a></li>\n"
                        + "<li><a href=\"orderhistory.html\">Order History</a></li>\n"
                        + "</ul>\n"
                        + "</nav>\n"
                        + "</div></div></div></div>";

            }

        } else {

            // Guest user
            headerHtml = "<div class=\"container\">\n"
                    + "<div class=\"row justify-content-center\">\n"
                    + "<div class=\"col-lg-12 col-md-12\">\n"
                    + "<div class=\"main_menu menu_position\">\n"
                    + "<nav>\n"
                    + "<ul>\n"
                    + "<li><a href=\"admin-user-management.html\">Home</a></li>\n"
                    + "<li><a href=\"productview.html\">Product</a></li>\n"
                    + "<li><a href=\"cart.html\">Cart</a></li>\n"
                    + "</ul>\n"
                    + "</nav>\n"
                    + "</div></div></div></div>";

        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(headerHtml);

    }

}
