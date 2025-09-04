/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "LoadTrendingProducts", urlPatterns = {"/LoadTrendingProducts"})
public class LoadTrendingProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session session = null;
        Transaction tx = null;

        try {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            
            session = sf.openSession();
            
            tx = session.beginTransaction();

            // Just fetch all, we'll filter manually
            List<Product> allProducts = session.createCriteria(Product.class).list();

            JsonArray jsonArray = new JsonArray();

            int count = 0;
            for (int i = allProducts.size() - 1; i >= 0 && count < 3; i--) {
                Product p = allProducts.get(i);
                JsonObject obj = new JsonObject();
                obj.addProperty("id", p.getId());
                obj.addProperty("name", p.getTitle());
                obj.addProperty("price", p.getPrice());
                obj.addProperty("image", "product-images/" + p.getId() + "/image1.png");
                jsonArray.add(obj);
                count++;
            }

            tx.commit();
            
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            
            if (tx != null) {
                
                tx.rollback();
                
            }
            
            JsonObject error = new JsonObject();
            
            error.addProperty("error", "Failed to load products.");
            
            response.setContentType("application/json");

            response.getWriter().write(error.toString());
            
        } finally {
            
            if (session != null) {
                
                session.close();
                
            }
            
        }
        
    }

}
