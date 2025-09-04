/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseJson = new JsonObject();

        HttpSession httpSession = request.getSession(false);
        
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            
            responseJson.addProperty("status", false);
            
            responseJson.addProperty("message", "User not logged in.");
            
            response.getWriter().write(responseJson.toString());
            
            return;
            
        }

        User user = (User) httpSession.getAttribute("user");

        Session hsession = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = hsession.beginTransaction();

        Criteria criteria = hsession.createCriteria(Cart.class);
        
        criteria.add(Restrictions.eq("user_id", user));
        
        List<Cart> cartItems = criteria.list();

        JsonArray jsonItems = new JsonArray();

        for (Cart cart : cartItems) {
            
            JsonObject item = new JsonObject();
            item.addProperty("qty", cart.getQty());

            Product product = cart.getProduct_id();
            JsonObject productJson = new JsonObject();
            productJson.addProperty("id", product.getId());
            productJson.addProperty("title", product.getTitle());
            productJson.addProperty("price", product.getPrice());

            item.add("product", productJson);
            jsonItems.add(item);
            
        }

        tx.commit();
        hsession.close();

        responseJson.addProperty("status", true);
        responseJson.add("cartItems", jsonItems);
        response.setContentType("application/json");
        response.getWriter().write(responseJson.toString());
        
    }

}
