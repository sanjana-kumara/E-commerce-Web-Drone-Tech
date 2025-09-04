/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
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
@WebServlet(name = "RemoveCartItem", urlPatterns = {"/RemoveCartItem"})
public class RemoveCartItem extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        
        JsonObject jsonResponse = new JsonObject();

        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
        
        int productId = requestJson.get("id").getAsInt();

        User sessionUser = (User) request.getSession().getAttribute("user");

        if (sessionUser == null) {
            
            jsonResponse.addProperty("status", false);
            
            jsonResponse.addProperty("message", "User not logged in");
            
            response.setContentType("application/json");
            
            response.getWriter().write(gson.toJson(jsonResponse));
            
            return;
            
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Transaction tx = session.beginTransaction();

        User attachedUser = (User) session.get(User.class, sessionUser.getId());

        // 5. Use Criteria to load Cart by user and product
        Criteria criteria = session.createCriteria(Cart.class);
        criteria.add(Restrictions.eq("user_id.id", attachedUser.getId()));
        criteria.add(Restrictions.eq("product_id.id", productId));

        @SuppressWarnings("unchecked")
        List<Cart> cartItems = criteria.list();

        Cart matchedCartItem = null;

        if (!cartItems.isEmpty()) {
            
            matchedCartItem = cartItems.get(0);
        }

        if (matchedCartItem != null) {
            
            session.delete(matchedCartItem);
            
            tx.commit();
            
            jsonResponse.addProperty("status", true);
            
        } else {
            
            jsonResponse.addProperty("status", false);
            
            jsonResponse.addProperty("message", "Cart item not found");
            
            tx.rollback();
            
        }

        session.close();

        response.setContentType("application/json");
        
        response.getWriter().write(gson.toJson(jsonResponse));
        
    }

}
