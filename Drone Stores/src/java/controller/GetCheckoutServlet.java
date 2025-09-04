/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.DiliveryType;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "GetCheckoutServlet", urlPatterns = {"/GetCheckoutServlet"})
public class GetCheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("user") : null;

        JsonObject responseJson = new JsonObject();

        if (user == null) {

            responseJson.addProperty("status", false);

            responseJson.addProperty("message", "User not logged in.");

            response.getWriter().write(new Gson().toJson(responseJson));

            return;

        }

        Session s = HibernateUtil.getSessionFactory().openSession();

        //Fetch Cart Items
        Criteria cartCriteria = s.createCriteria(Cart.class);

        cartCriteria.add(Restrictions.eq("user_id", user));

        List<Cart> cartItems = cartCriteria.list();

        JsonArray cartArray = new JsonArray();
        double subtotal = 0;

        for (Cart cart : cartItems) {

            Product product = cart.getProduct_id();

            int qty = cart.getQty();

            double total = product.getPrice() * qty;

            JsonObject item = new JsonObject();

            item.addProperty("product", product.getTitle());

            item.addProperty("qty", qty);

            item.addProperty("total", total);

            cartArray.add(item);

            subtotal += total;

        }

        //Get User's Address
        Criteria addressCriteria = s.createCriteria(Address.class);

        addressCriteria.add(Restrictions.eq("user_id", user));

        addressCriteria.setMaxResults(1);

        Address address = (Address) addressCriteria.uniqueResult();

        double shipping = 0;
        String deliveryTypeName = "";

        if (address != null && address.getCity_id() != null) {

            String cityName = address.getCity_id().getName();

            int deliveryTypeId = cityName.equalsIgnoreCase("Colombo") ? 1 : 2;

            DiliveryType diliveryType = (DiliveryType) s.get(DiliveryType.class, deliveryTypeId);

            if (diliveryType != null) {

                shipping = diliveryType.getPrice();

                deliveryTypeName = diliveryType.getName();

            }

        }

        double grandtotal = subtotal + shipping;

        // Build response
        responseJson.addProperty("status", true);
        responseJson.add("cart", cartArray);
        responseJson.addProperty("subtotal", subtotal);
        responseJson.addProperty("shipping", shipping);
        responseJson.addProperty("shipping_type", deliveryTypeName);
        responseJson.addProperty("grandtotal", grandtotal);

        s.close();
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseJson));

    }

}
