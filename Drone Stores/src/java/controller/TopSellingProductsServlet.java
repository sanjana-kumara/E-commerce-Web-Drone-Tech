/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "TopSellingProductsServlet", urlPatterns = {"/TopSellingProductsServlet"})
public class TopSellingProductsServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        Session session = HibernateUtil.getSessionFactory().openSession(); 
        
        Criteria criteria = session.createCriteria(OrderItems.class);

        @SuppressWarnings("unchecked")
        List<OrderItems> allItems = criteria.list();

        // Aggregate qty per product
        Map<Product, Integer> productSales = new HashMap<>();
        for (OrderItems item : allItems) {
            Product product = item.getProduct();
            productSales.put(product,
                    productSales.getOrDefault(product, 0) + item.getQty());
        }

        // Sort & limit to top 5
        List<Map<String, Object>> topProducts = productSales.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", entry.getKey().getId());
                    map.put("title", entry.getKey().getTitle());
                    map.put("qty", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        session.close(); // close manually
        resp.getWriter().write(gson.toJson(topProducts));
    }

}
