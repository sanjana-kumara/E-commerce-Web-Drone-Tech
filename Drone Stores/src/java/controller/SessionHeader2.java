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
@WebServlet(name = "SessionHeader2", urlPatterns = {"/SessionHeader2"})
public class SessionHeader2 extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String headerHtml = "";

        if (session != null && session.getAttribute("user") != null) {
            User u = (User) session.getAttribute("user");

            if ("Verified".equalsIgnoreCase(u.getVerification())) {

                headerHtml = "<div class=\"container\">"
                        + "<div class=\"row align-items-center\">"
                        + "<div class=\"col-lg-3 col-md-6\">"
                        + "<div class=\"logo\">"
                        + "<a href=\"index.html\" class=\"logo_name\">"
                        + "<img src=\"assets/img/logo/logo6.png\" alt=\"\">"
                        + "<h3>Dronetech</h3>"
                        + "</a></div></div>"
                        + "<div class=\"col-lg-9 col-md-6\">"
                        + "<div class=\"middel_right\">"
                        + "<div class=\"search_container position-relative\">"
                        + "<form action=\"#\">"
                        + "<div class=\"search_box\">"
                        + "<input placeholder=\"Search product...\" type=\"text\" id=\"searchInput\" autocomplete=\"off\">"
                        + "<button type=\"submit\">Search</button>"
                        + "</div></form></div>"
                        + "<div class=\"middel_right_info gap-2\">"
                        + "<div class=\"mini_cart_wrapper\">"
                        + "<img src=\"assets/img/user.png\" alt=\"Profile Image\" style=\"width:40px; height:40px; border-radius:50%;\" />"
                        + "<div class=\"mini_cart\">"
                        + "<div class=\"mini_cart_footer\">"
                        + "<div class=\"cart_button\"><a href=\"my-account.html\">My Profile</a></div>"
                        + "<div class=\"cart_button\"><a href=\"cart.html\">View Cart</a></div><br>"
                        + "<div class=\"cart_button\"><a href=\"checkout.html\">Check Out</a></div><br>"
                        + "<div class=\"cart_button\"><a onclick=\"signOut();\">Log Out</a></div>"
                        + "</div></div></div></div></div></div></div>";

            } else if ("Admin".equalsIgnoreCase(u.getVerification())) {

                headerHtml = "<div class=\"container\">"
                        + "<div class=\"row align-items-center\">"
                        + "<div class=\"col-lg-3 col-md-6\">"
                        + "<div class=\"logo\">"
                        + "<a href=\"admin-user-management.html\" class=\"logo_name\">"
                        + "<img src=\"assets/img/logo/logo6.png\" alt=\"\">"
                        + "<h3>Dronetech</h3>"
                        + "</a></div></div>"
                        + "<div class=\"col-lg-9 col-md-6\">"
                        + "<h1 class=\"text-white\" style=\"font-size: 48px; font-weight: bold; margin-top: 10px;\">Admin Panel</h1>"
                        + "</div></div></div>";
            }

        } else {

            headerHtml = "<div class=\"container\">"
                    + "<div class=\"row align-items-center\">"
                    + "<div class=\"col-lg-3 col-md-6\">"
                    + "<div class=\"logo\">"
                    + "<a href=\"index.html\" class=\"logo_name\">"
                    + "<img src=\"assets/img/logo/logo6.png\" alt=\"\">"
                    + "<h3>Dronetech</h3>"
                    + "</a></div></div>"
                    + "<div class=\"col-lg-9 col-md-6\">"
                    + "<div class=\"middel_right\">"
                    + "<div class=\"search_container\">"
                    + "<form action=\"#\">"
                    + "<div class=\"search_box\">"
                    + "<input placeholder=\"Search product...\" type=\"text\">"
                    + "<button type=\"submit\">Search</button>"
                    + "</div></form></div>"
                    + "</div></div></div></div>";

        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(headerHtml);

    }

}
