/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.Categories;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Model;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "LoadFiltersServlet", urlPatterns = {"/LoadFiltersServlet"})
public class LoadFiltersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Gson gson = new Gson();
        JsonObject resp = new JsonObject();

        // Load brands
        List<Brand> brands = session.createCriteria(Brand.class).list();
        resp.add("brands", gson.toJsonTree(brands));

        // Load categories
        List<Categories> categories = session.createCriteria(Categories.class).list();
        resp.add("categories", gson.toJsonTree(categories));

        // Load colors
        List<Color> colors = session.createCriteria(Color.class).list();
        resp.add("colors", gson.toJsonTree(colors));

        // Load models
        List<Model> models = session.createCriteria(Model.class).list();
        resp.add("models", gson.toJsonTree(models));

        resp.addProperty("status", true);

        response.setContentType("application/json");
        response.getWriter().write(resp.toString());

        session.close(); 
        
    }
    
}
