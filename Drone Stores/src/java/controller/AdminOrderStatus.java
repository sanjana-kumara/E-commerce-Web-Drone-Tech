/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "AdminOrderStatus", urlPatterns = {"/AdminOrderStatus"})
public class AdminOrderStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Criteria criteria = session.createCriteria(OrderItems.class);

            List<OrderItems> orderItemsList = criteria.list();

            JsonArray orderArray = new JsonArray();

            for (OrderItems oi : orderItemsList) {

                JsonObject obj = new JsonObject();
                obj.addProperty("orderItemId", oi.getId());
                obj.addProperty("orderId", oi.getOrders().getId());

                String fullName = oi.getOrders().getUser().getFirst_name()+ " " + oi.getOrders().getUser().getLast_name();
                obj.addProperty("customerName", fullName);

                obj.addProperty("status", oi.getOrderStatus().getValue());
                obj.addProperty("date", oi.getOrders().getCreated_at().toString());

                obj.addProperty("productName", oi.getProduct().getTitle());
                obj.addProperty("price", oi.getProduct().getPrice());
                obj.addProperty("qty", oi.getQty());

                orderArray.add(obj);

            }

            response.setContentType("application/json");

            response.getWriter().write(orderArray.toString());

        } finally {

            if (session != null && session.isOpen()) {
                session.close();

            }

        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int orderItemId = Integer.parseInt(request.getParameter("orderItemId"));

        String newStatus = request.getParameter("status");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {

            tx = session.beginTransaction();

            Criteria criteriaItem = session.createCriteria(OrderItems.class);
            criteriaItem.add(Restrictions.eq("id", orderItemId));
            OrderItems item = (OrderItems) criteriaItem.uniqueResult();

            Criteria criteriaStatus = session.createCriteria(OrderStatus.class);
            criteriaStatus.add(Restrictions.eq("value", newStatus));
            OrderStatus status = (OrderStatus) criteriaStatus.uniqueResult();

            if (item != null && status != null) {

                item.setOrderStatus(status);

                session.update(item);

                tx.commit();

                response.getWriter().write("success");

            } else {

                if (tx != null) {

                    tx.rollback();

                }

                response.getWriter().write("fail");

            }

        } finally {

            if (session != null && session.isOpen()) {

                session.close();

            }

        }

    }

}
