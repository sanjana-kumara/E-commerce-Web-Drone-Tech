/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Status;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "ProductApprove", urlPatterns = {"/ProductApprove"})
public class ProductApprove extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject json = new JsonObject();

        String idStr = request.getParameter("id");

        System.out.println(idStr);

        if (idStr == null) {

            json.addProperty("status", false);

            json.addProperty("message", "Missing product ID");

            response.getWriter().write(json.toString());

            return;

        }

        int productId = Integer.parseInt(idStr);

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Transaction tx = session.beginTransaction();

        Product product = (Product) session.get(Product.class, productId);

        if (product != null) {

            Status status = (Status) session.get(Status.class, 2); // Status ID 2 = Approved

            product.setStatus_id(status);

            session.update(product);

            tx.commit();

            json.addProperty("status", true);

        } else {

            json.addProperty("status", false);

            json.addProperty("message", "Product not found");

        }

        session.close();
        
        response.setContentType("application/json");

        response.getWriter().write(json.toString());

    }

}
