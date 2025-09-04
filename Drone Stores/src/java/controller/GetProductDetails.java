/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "GetProductDetails", urlPatterns = {"/GetProductDetails"})
public class GetProductDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();

        if (idParam == null || idParam.trim().isEmpty()) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Product ID not provided.");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            
            int productId = Integer.parseInt(idParam);

            Product product = (Product) session.get(Product.class, productId);

            if (product != null) {
                
                // Remove unnecessary data
                product.setUser_id(null);

                JsonObject productJson = new JsonObject();
                productJson.addProperty("id", product.getId());
                productJson.addProperty("title", product.getTitle());
                productJson.addProperty("price", product.getPrice());
                productJson.addProperty("qty", product.getQty());
                productJson.addProperty("description", product.getDescription() != null ? product.getDescription() : "");
                productJson.addProperty("flightTime", product.getFilght_time()!= null ? product.getFilght_time(): "");
                productJson.addProperty("range", product.getRange() != null ? product.getRange() : "");
                productJson.addProperty("features", product.getFeautures()!= null ? product.getFeautures(): "");
                productJson.addProperty("warranty", product.getWarranty() != null ? product.getWarranty() : "");

                Brand brand = product.getBrand_id();
                Model model = product.getModel_id();
                Color color = product.getColor_id();

                JsonObject brandJson = new JsonObject();
                brandJson.addProperty("id", brand.getId());
                brandJson.addProperty("name", brand.getName());

                JsonObject modelJson = new JsonObject();
                modelJson.addProperty("id", model.getId());
                modelJson.addProperty("name", model.getName());

                JsonObject colorJson = new JsonObject();
                colorJson.addProperty("name", color.getName());
                
                productJson.add("brand", brandJson);
                productJson.add("model", modelJson);
                productJson.add("color", colorJson);

                responseObject.addProperty("status", true);
                responseObject.add("product", productJson);

            } else {
                
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Product not found.");
                
            }

        } catch (NumberFormatException e) {
            
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Invalid product ID.");
            
        } catch (Exception e) {
            
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Server error.");
            
            e.printStackTrace();
            
        } finally {
            
            session.close();
            
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
        
    }

}
