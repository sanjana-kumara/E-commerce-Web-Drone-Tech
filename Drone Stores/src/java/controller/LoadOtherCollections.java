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
import org.hibernate.Transaction;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "LoadOtherCollections", urlPatterns = {"/LoadOtherCollections"})
public class LoadOtherCollections extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Transaction tx = session.beginTransaction();

        List<Product> productList = session.createCriteria(Product.class).setMaxResults(12).list();

        JsonArray jsonArray = new JsonArray();
        
        for (int i = productList.size() - 1; i >= 0; i--) {
            
            Product p = productList.get(i);

            JsonObject obj = new JsonObject();
            obj.addProperty("id", p.getId());
            obj.addProperty("name", p.getTitle());
            obj.addProperty("price", p.getPrice());
            obj.addProperty("image", "product-images/" + p.getId() + "/image1.png");

            jsonArray.add(obj);
            
        }

        tx.commit();
        session.close();
        
        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
        
    }

}
