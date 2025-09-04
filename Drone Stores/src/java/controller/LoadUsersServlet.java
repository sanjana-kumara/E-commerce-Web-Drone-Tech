/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "LoadUsersServlet", urlPatterns = {"/LoadUsersServlet"})
public class LoadUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        List<User> users = null;

        Criteria criteria = session.createCriteria(User.class);
        
        criteria.add(Restrictions.ne("verification", "Admin")); // verification != 'Admin'
        
        users = criteria.list();

        session.close();

        JsonArray jsonArray = new JsonArray();

        for (User user : users) {

            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("id", user.getId());
            jsonUser.addProperty("first_name", user.getFirst_name());
            jsonUser.addProperty("last_name", user.getLast_name());
            jsonUser.addProperty("email", user.getEmail());
            jsonUser.addProperty("verification", user.getVerification());
            jsonArray.add(jsonUser);

        }

        response.getWriter().write(jsonArray.toString());

    }

}
