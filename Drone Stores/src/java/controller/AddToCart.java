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
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String prId = request.getParameter("prId");
        String qty = request.getParameter("qty");
        
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(prId)) {
            
            responseObject.addProperty("message", "Invalid product Id!");
            
        } else if (!Util.isInteger(qty)) {
            
            responseObject.addProperty("message", "Invalid product Quantity!");
            
        } else {
            
            int productId = Integer.parseInt(prId);
            int quantity = Integer.parseInt(qty);

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            try {
                
                Product product = (Product) session.get(Product.class, productId);
                
                if (product == null) {
                    
                    responseObject.addProperty("message", "Product not found!");
                    
                } else if (quantity > product.getQty()) {
                    
                    responseObject.addProperty("message", "OOPS... Insufficient Product quantity!");
                    
                } else {
                    
                    User user = (User) request.getSession().getAttribute("user");

                    if (user != null) {
                        
                        mergeSessionCartToDb(request.getSession(), user, session); // Merge session to DB
                        handleLoggedInCart(session, product, user, quantity, responseObject);
                        transaction.commit();
                        
                    } else {
                        
                        handleSessionCart(request.getSession(), product, quantity, responseObject);
                        
                    }
                    
                }
                
            } catch (Exception e) {
                
                transaction.rollback();
                
                e.printStackTrace();
                
                responseObject.addProperty("message", "Something went wrong!");
                
            } finally {
                
                session.close();
                
            }
            
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
        
    }

    // Logged-in Cart Logic
    private void handleLoggedInCart(Session session, Product product, User user, int qty, JsonObject response) {
        
        Criteria criteria = session.createCriteria(Cart.class);
        
        criteria.add(Restrictions.eq("user_id", user));      // use 'user_id' property name
        
        criteria.add(Restrictions.eq("product_id", product)); // use 'product_id' property name

        Cart cart = (Cart) criteria.uniqueResult();

        if (cart != null) {
            
            int updatedQty = cart.getQty() + qty;
            
            if (updatedQty <= product.getQty()) {
                
                cart.setQty(updatedQty);
                
                session.update(cart);
                
                response.addProperty("status", true);
                
                response.addProperty("message", "Product cart successfully updated");
                
            } else {
                
                response.addProperty("message", "OOPS... Insufficient Product quantity!");
                
            }
            
        } else {
            
            Cart newCart = new Cart();
            
            newCart.setProduct_id(product);
            
            newCart.setUser_id(user);
            
            newCart.setQty(qty);
            
            session.save(newCart);
            
            response.addProperty("status", true);
            
            response.addProperty("message", "Product added to cart successfully");
            
        }
        
    }

    // Guest Cart Logic (stored in session)
    private void handleSessionCart(HttpSession httpSession, Product product, int qty, JsonObject response) {
        
        ArrayList<Cart> cartList = (ArrayList<Cart>) httpSession.getAttribute("sessionCart");

        if (cartList == null) {
            
            cartList = new ArrayList<>();
            
            httpSession.setAttribute("sessionCart", cartList);
            
        }

        for (Cart item : cartList) {
            
            if (item.getProduct_id().getId() == product.getId()) {  // use getProduct_id()
                
                int updatedQty = item.getQty() + qty;
                
                if (updatedQty <= product.getQty()) {
                    
                    item.setQty(updatedQty);
                    
                    response.addProperty("status", true);
                    
                    response.addProperty("message", "Product cart updated");
                    
                } else {
                    
                    response.addProperty("message", "OOPS... Insufficient Product quantity!");
                    
                }
                
                return;
                
            }
            
        }

        if (qty <= product.getQty()) {
            
            Cart newCart = new Cart();
            
            newCart.setProduct_id(product);
            
            newCart.setQty(qty);
            
            newCart.setUser_id(null);  // guest, no user
            
            cartList.add(newCart);
            
            response.addProperty("status", true);
            
            response.addProperty("message", "Product added to cart");
            
        } else {
            
            response.addProperty("message", "OOPS... Insufficient Product quantity!");
            
        }
        
    }

    // Merge session cart to DB cart on login
    private void mergeSessionCartToDb(HttpSession httpSession, User user, Session session) {
        
        ArrayList<Cart> sessionCarts = (ArrayList<Cart>) httpSession.getAttribute("sessionCart");
        
        if (sessionCarts == null || sessionCarts.isEmpty()) {
            
            return;
            
        }
        
        for (Cart sessCart : sessionCarts) {
            
            Product product = (Product) session.get(Product.class, sessCart.getProduct_id().getId());

            Criteria criteria = session.createCriteria(Cart.class);
            
            criteria.add(Restrictions.eq("user_id", user));      // fix property name
            
            criteria.add(Restrictions.eq("product_id", product)); // fix property name

            Cart dbCart = (Cart) criteria.uniqueResult();

            if (dbCart != null) {
                
                int combinedQty = dbCart.getQty() + sessCart.getQty();
                
                dbCart.setQty(Math.min(combinedQty, product.getQty()));
                
                session.update(dbCart);
                
            } else {
                
                Cart newCart = new Cart();
                
                newCart.setUser_id(user);
                
                newCart.setProduct_id(product);
                
                newCart.setQty(Math.min(sessCart.getQty(), product.getQty()));
                
                session.save(newCart);
                
            }
            
        }
        
        httpSession.removeAttribute("sessionCart"); // Clear after merging
        
    }
    
}
