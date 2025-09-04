package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/my-account.html", "/product-details", "/wishilst.html", "/services.html", "/checkout.html", "/productadd.html", "/admin-user-management.html", "/admin-product-management.html","/admin-order-management.html"}) //http://localhost:8080/SmartTrader/my-account.html
public class SigninCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;  // casting -> Downcasting

        HttpServletResponse response = (HttpServletResponse) res; //casting

        HttpSession ses = request.getSession(false);

        //cash clear
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        response.setHeader("Pragma", "no-cache");

        response.setHeader("Expires", "0");
        //cash clear

        if (ses != null && ses.getAttribute("user") != null) {

            chain.doFilter(request, response);

        } else {

            response.sendRedirect("signin.html");

        }

    }

    @Override
    public void destroy() {
    }

}
