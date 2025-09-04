package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.Categories;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Product;
import hibernate.Status;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SearchProductServlet", urlPatterns = {"/SearchProductServlet"})
public class SearchProductServlet extends HttpServlet {

    private static final int PAGE_SIZE = 6; // Number of products per page

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();

        responseObject.addProperty("status", false);

        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Criteria productCriteria = session.createCriteria(Product.class);

            // ---------- Filter: Brand (Multiple) ----------
            if (requestObject.has("brands")) {

                JsonArray brandArray = requestObject.getAsJsonArray("brands");

                if (brandArray.size() > 0) {

                    List<Brand> brands = new ArrayList<>();

                    for (JsonElement el : brandArray) {

                        String brandStr = el.getAsString();

                        if (Util.isInteger(brandStr)) {

                            int brandId = Integer.parseInt(brandStr);
                            Brand brand = (Brand) session.get(Brand.class, brandId);

                            if (brand != null) {

                                brands.add(brand);

                            }

                        }

                    }

                    if (!brands.isEmpty()) {

                        productCriteria.add(Restrictions.in("brand_id", brands));

                    }

                }

            }

            // ---------- Filter: Model ----------
            if (requestObject.has("model")) {

                String modelStr = requestObject.get("model").getAsString();

                if (Util.isInteger(modelStr)) {

                    int modelId = Integer.parseInt(modelStr);

                    Model model = (Model) session.get(Model.class, modelId);

                    if (model != null) {

                        productCriteria.add(Restrictions.eq("model_id", model));

                    }

                }

            }

            // ---------- Filter: Category ----------
            if (requestObject.has("category")) {

                String categoryStr = requestObject.get("category").getAsString();

                if (Util.isInteger(categoryStr)) {

                    int categoryId = Integer.parseInt(categoryStr);

                    Categories category = (Categories) session.get(Categories.class, categoryId);

                    if (category != null) {

                        productCriteria.add(Restrictions.eq("categories_id", category));

                    }

                }

            }

            // ---------- Filter: Color ----------
            if (requestObject.has("color")) {

                String colorStr = requestObject.get("color").getAsString();

                if (Util.isInteger(colorStr)) {

                    int colorId = Integer.parseInt(colorStr);

                    Color color = (Color) session.get(Color.class, colorId);

                    if (color != null) {

                        productCriteria.add(Restrictions.eq("color_id", color));

                    }

                }

            }

            // ---------- Filter: Price Range ----------
            if (requestObject.has("minPrice") && requestObject.has("maxPrice")) {

                String minStr = requestObject.get("minPrice").getAsString();

                String maxStr = requestObject.get("maxPrice").getAsString();

                if (Util.isDouble(minStr) && Util.isDouble(maxStr)) {

                    double minPrice = Double.parseDouble(minStr);

                    double maxPrice = Double.parseDouble(maxStr);

                    productCriteria.add(Restrictions.between("price", minPrice, maxPrice));

                }
            }

            // ---------- Filter: Only Active Status ----------
            Status activeStatus = (Status) session.get(Status.class, 2); // status_id = 2 => Active

            if (activeStatus != null) {

                productCriteria.add(Restrictions.eq("status_id", activeStatus));

            }

            // ---------- Pagination ----------
            int offset = 0;
            if (requestObject.has("offset") && Util.isInteger(requestObject.get("offset").getAsString())) {
                offset = requestObject.get("offset").getAsInt();
            }

            productCriteria.setFirstResult(offset);
            productCriteria.setMaxResults(PAGE_SIZE);

            List<Product> resultList = productCriteria.list();
            List<Product> cleaned = new ArrayList<>();

            for (Product p : resultList) {
                p.setUser_id(null); // Remove user info if not needed
                cleaned.add(p);

            }

            responseObject.add("productList", gson.toJsonTree(cleaned));

            responseObject.addProperty("allProductCount", productCriteria.list().size());

            System.out.println(productCriteria.list().size());

            responseObject.addProperty("status", true);

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            session.close();

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }
}
