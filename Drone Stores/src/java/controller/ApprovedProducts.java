/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
@WebServlet(name = "ApprovedProducts", urlPatterns = {"/ApprovedProducts"})
public class ApprovedProducts extends HttpServlet {

     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Product> allProducts = session.createCriteria(Product.class).list();
        List<Product> approvedProducts = new ArrayList<>();

        for (Product product : allProducts) {
            
            if (product.getStatus_id() != null && product.getStatus_id().getId() == 1) {
                
                approvedProducts.add(product);
                
            }
            
        }

        response.setContentType("application/json");
        
        response.getWriter().write(new Gson().toJson(approvedProducts));

        session.close();
        
    }
    
}
