/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "SearchSuggestions", urlPatterns = {"/SearchSuggestions"})
public class SearchSuggestions extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("query");
        List<String> productTitles;

        org.hibernate.Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(hibernate.Product.class);
        criteria.add(Restrictions.ilike("title", "%" + query.trim() + "%"));
        criteria.setProjection(Projections.property("title"));
        criteria.setMaxResults(10);

        productTitles = criteria.list();
        session.close();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(new com.google.gson.Gson().toJson(productTitles));
        out.flush();
    }

}
