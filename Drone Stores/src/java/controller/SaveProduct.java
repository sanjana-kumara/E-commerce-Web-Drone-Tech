/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.Categories;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Product;
import hibernate.Status;
import hibernate.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();

        responseObject.addProperty("status", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        // Load categories using Criteria
        Criteria categorieslist = session.createCriteria(Categories.class);
        List<Categories> categories = categorieslist.list();

        // Load colors using Criteria
        Criteria colorsList = session.createCriteria(Color.class);
        List<Color> colors = colorsList.list();

        // Load brand using Criteria
        Criteria brandList = session.createCriteria(Brand.class);
        List<Brand> brand = brandList.list();

        // Load colors using Criteria
        Criteria modelList = session.createCriteria(Model.class);
        List<Model> model = modelList.list();

        responseObject.add("categories", gson.toJsonTree(categories));
        responseObject.add("colors", gson.toJsonTree(colors));
        responseObject.add("brand", gson.toJsonTree(brand));
        responseObject.add("model", gson.toJsonTree(model));

        responseObject.addProperty("status", true);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

        session.close();

    }

    private static final int PENDING_STAUTUS_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        // Get all fields
        String product_Name = request.getParameter("product_Name");
        String product_Category = request.getParameter("product_Category");
        String product_Brand = request.getParameter("product_Brand");
        String product_Model = request.getParameter("product_Model");
        String product_Color = request.getParameter("product_Color");
        String product_Price = request.getParameter("product_Price");
        String product_Qty = request.getParameter("product_Qty");
        String product_Flight_Time = request.getParameter("product_Flight_Time");
        String product_Range = request.getParameter("product_Range");
        String product_Warranty = request.getParameter("product_Warranty");
        String product_Feature = request.getParameter("product_Feature");
        String product_Description = request.getParameter("product_Description");

        Part part1 = request.getPart("image1");
        Part part2 = request.getPart("image2");
        Part part3 = request.getPart("image3");

        Session session = HibernateUtil.getSessionFactory().openSession();

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {

            responseObject.addProperty("message", "Please sign in first.");

        } else if (product_Name == null || product_Name.trim().isEmpty()
                || product_Category == null || product_Brand == null || product_Model == null || product_Color == null
                || product_Price == null || product_Qty == null
                || product_Flight_Time == null || product_Range == null || product_Warranty == null
                || product_Feature == null || product_Description == null
                || part1 == null || part1.getSize() == 0
                || part2 == null || part2.getSize() == 0
                || part3 == null || part3.getSize() == 0) {

            responseObject.addProperty("message", "All fields including all 3 images are required.");

        } else if (!Util.isInteger(product_Brand) || !Util.isInteger(product_Model)
                || !Util.isInteger(product_Color) || !Util.isInteger(product_Category)) {

            responseObject.addProperty("message", "Invalid brand/model/color/category selection.");

        } else if (!Util.isDouble(product_Price) || Double.parseDouble(product_Price) <= 0) {

            responseObject.addProperty("message", "Invalid price.");

        } else if (!Util.isInteger(product_Qty) || Integer.parseInt(product_Qty) <= 0) {

            responseObject.addProperty("message", "Invalid quantity.");

        } else {

            // Load foreign key entities
            Brand brand = (Brand) session.get(Brand.class, Integer.parseInt(product_Brand));
            Model model = (Model) session.get(Model.class, Integer.parseInt(product_Model));
            Color color = (Color) session.get(Color.class, Integer.parseInt(product_Color));
            Categories category = (Categories) session.get(Categories.class, Integer.parseInt(product_Category));
            Status status = (Status) session.get(Status.class, PENDING_STAUTUS_ID);

            if (brand == null || model == null || color == null || category == null) {
                responseObject.addProperty("message", "Invalid foreign key data.");
            } else {

                // Save Product
                Product p = new Product();
                p.setTitle(product_Name.trim());
                p.setDescription(product_Description.trim());
                p.setBrand_id(brand);
                p.setModel_id(model);
                p.setCategories_id(category);
                p.setColor_id(color);
                p.setPrice(Double.parseDouble(product_Price));
                p.setQty(Integer.parseInt(product_Qty));
                p.setFilght_time(product_Flight_Time.trim());
                p.setRange(product_Range.trim());
                p.setWarranty(product_Warranty.trim());
                p.setFeautures(product_Feature.trim());
                p.setCreated_at(new Date());
                p.setStatus_id(status);
                p.setUser_id(user);

                session.beginTransaction();
                int productId = (int) session.save(p);
                session.getTransaction().commit();

                // Upload images
                String appPath = getServletContext().getRealPath("");
                String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");

                File productFolder = new File(newPath, String.valueOf(productId));
                productFolder.mkdirs();

                Files.copy(part1.getInputStream(), new File(productFolder, "image1.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(part2.getInputStream(), new File(productFolder, "image2.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(part3.getInputStream(), new File(productFolder, "image3.png").toPath(), StandardCopyOption.REPLACE_EXISTING);

                System.out.println("App Path: " + appPath);
                System.out.println("Image Folder Path: " + newPath);
                System.out.println("Product Folder Path: " + productFolder);

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Product successfully added.");

            }

        }

        session.close();
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));

    }

}
