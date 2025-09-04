package model;

import hibernate.User;
import hibernate.User;
import hibernate.User;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

// Me pages walata access karanne nathi wenna one verified login user la
@WebFilter(urlPatterns = {"/signin.html", "/signup.html", "/account-verify.html"})
public class GuestOnlyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        // session thiyenawanam verified user ekak nam index.html ta redirect karanawa
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            String verification = user.getVerification();

            if ("Verified".equalsIgnoreCase(verification)) {
                
                res.sendRedirect("index.html");
                
                return;
                

            } else if ("Admin".equalsIgnoreCase(verification)) {
                
                 res.sendRedirect("signup.html");
                
                return;
                
            }
            
        }

        // nathnam normal una widiya page eka display wenawa
        chain.doFilter(request, response);
        
    }
    
}
