package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/profile-images/*")
public class ProfileImageSecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Login required to view profile images.");
            return;
        }

        Boolean verified = (Boolean) session.getAttribute("verified");
        if (verified == null || !verified) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not verified.");
            return;
        }

        // Proceed to allow access
        chain.doFilter(req, resp);
    }
}
