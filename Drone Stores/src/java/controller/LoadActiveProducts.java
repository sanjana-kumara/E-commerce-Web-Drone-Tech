/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "LoadActiveProducts", urlPatterns = {"/LoadActiveProducts"})
public class LoadActiveProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        // Fetch products where related status name = "Active"
        Criteria criteria = session.createCriteria(Product.class).add(Restrictions.eq("status_id.id", 2));

        List<Product> activeProducts = criteria.list();
        session.close();

        // Convert to JSON
        String json = new Gson().toJson(activeProducts);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pid = request.getParameter("productId");
        
        JsonObject responseObject = new JsonObject();
        
        Gson gson = new Gson();

        if (pid == null || pid.trim().isEmpty()) {
            
            responseObject.addProperty("status", false);
            
            responseObject.addProperty("message", "Missing productId");
            
            response.setContentType("application/json");
            
            response.getWriter().write(gson.toJson(responseObject));
            
            return;
            
        }

        int productId;
        
        try {
            
            productId = Integer.parseInt(pid);
            
        } catch (NumberFormatException e) {
            
            responseObject.addProperty("status", false);
            
            responseObject.addProperty("message", "Invalid productId");
            
            response.setContentType("application/json");
            
            response.getWriter().write(gson.toJson(responseObject));
            
            return;
            
            
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            
            session.beginTransaction();

            Criteria productCriteria = session.createCriteria(Product.class)
                    .add(Restrictions.eq("id", productId));

            Product product = (Product) productCriteria.uniqueResult();

            if (product != null) {
                
                int deactivateID = 1; // Inactive status id

                Criteria statusCriteria = session.createCriteria(hibernate.Status.class)
                        .add(Restrictions.eq("id", deactivateID));

                hibernate.Status deactivateStatus = (hibernate.Status) statusCriteria.uniqueResult();

                if (deactivateStatus != null) {
                    
                    product.setStatus_id(deactivateStatus);
                    
                    session.update(product);
                    
                    session.getTransaction().commit();

                    responseObject.addProperty("status", true);
                    
                    responseObject.addProperty("message", "Product deactivated successfully");
                    
                } else {
                    
                    session.getTransaction().rollback();

                    responseObject.addProperty("status", false);
                    
                    responseObject.addProperty("message", "Inactive status not found");
                    
                }
                
            } else {
                
                session.getTransaction().rollback();

                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Product not found");
                
            }

        } finally {
            
            if (session.getTransaction().isActive()) {
                
                session.getTransaction().rollback();
            }
            
            session.close();
            
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
        
    }

}
