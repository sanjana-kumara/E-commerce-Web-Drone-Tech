/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.User;
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
@WebServlet(name = "OrderHistory", urlPatterns = {"/OrderHistory"})
public class OrderHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        User user = (User) request.getSession().getAttribute("user");

        Criteria criteria = session.createCriteria(OrderItems.class, "oi");

        criteria.createAlias("oi.orders", "o");

        criteria.add(Restrictions.eq("o.user", user));

        List<OrderItems> orderItemsList = criteria.list();

        JsonArray jsonArray = new JsonArray();

        for (OrderItems oi : orderItemsList) {

            JsonObject obj = new JsonObject();

            obj.addProperty("id", oi.getOrders().getId());

            obj.addProperty("created_at", oi.getOrders().getCreated_at().toString());

            obj.addProperty("items", oi.getProduct().getTitle() + " x" + oi.getQty());

            obj.addProperty("total", oi.getProduct().getPrice() * oi.getQty());

            obj.addProperty("status", oi.getOrderStatus().getValue());

            jsonArray.add(obj);

        }

        response.setContentType("application/json");

        response.getWriter().write(jsonArray.toString());

        session.close();

    }

}
