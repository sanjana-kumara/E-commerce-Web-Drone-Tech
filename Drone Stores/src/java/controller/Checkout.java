/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DiliveryType;
import hibernate.Distric;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Orders;
import hibernate.Product;
import hibernate.Province;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.PayHere;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    private static final int ORDER_PENDING_ID = 5;
    private static final int DELIVERY_WITHIN_COLOMBO = 1;
    private static final int DELIVERY_OUT_OF_COLOMBO = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject reqJson = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();

        HttpSession httpSession = request.getSession();

        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Session expired. Please log in again.");
            response.getWriter().write(gson.toJson(responseObject));
            return;
        }

        // Extract fields from JSON
        String name = reqJson.get("Name").getAsString();
        String email = reqJson.get("Email").getAsString();
        String line1 = reqJson.get("AdLine1").getAsString();
        String line2 = reqJson.get("AdLine2").getAsString();
        String districtName = reqJson.get("Distric").getAsString();
        String cityName = reqJson.get("city").getAsString();
        String provinceName = reqJson.get("Province").getAsString();
        String postalCode = reqJson.get("PostalCode").getAsString();
        String mobile = reqJson.get("MobileNo").getAsString();

        // Validate all fields
        if (name.isEmpty() || email.isEmpty() || line1.isEmpty() || line2.isEmpty()
                || districtName.isEmpty() || cityName.isEmpty() || provinceName.isEmpty()
                || postalCode.isEmpty() || mobile.isEmpty()) {

            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "All fields are required.");
            response.getWriter().write(gson.toJson(responseObject));
            return;
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();

        try {

            // Find Province by name
            Criteria provinceCriteria = session.createCriteria(Province.class);
            provinceCriteria.add(Restrictions.eq("name", provinceName));
            Province province = (Province) provinceCriteria.uniqueResult();

            if (province == null) {
                throw new Exception("Invalid province name");
            }

            // Find Distric by name and province
            Criteria districCriteria = session.createCriteria(Distric.class);
            districCriteria.add(Restrictions.eq("name", districtName));
            districCriteria.add(Restrictions.eq("province_id", province));
            Distric distric = (Distric) districCriteria.uniqueResult();

            if (distric == null) {
                throw new Exception("Invalid district name for given province");
            }

            // Find City by name and district
            Criteria cityCriteria = session.createCriteria(City.class);
            cityCriteria.add(Restrictions.eq("name", cityName));
            cityCriteria.add(Restrictions.eq("distric_id", distric));
            City city = (City) cityCriteria.uniqueResult();

            if (city == null) {
                throw new Exception("Invalid city name for given district");
            }

            // Get pending order status and delivery types
            OrderStatus pendingStatus = (OrderStatus) session.get(OrderStatus.class, ORDER_PENDING_ID);
            DiliveryType withinColombo = (DiliveryType) session.get(DiliveryType.class, DELIVERY_WITHIN_COLOMBO);
            DiliveryType outColombo = (DiliveryType) session.get(DiliveryType.class, DELIVERY_OUT_OF_COLOMBO);

            // Check if address already exists for this user with all given details
            Criteria addressCriteria = session.createCriteria(Address.class);
            addressCriteria.add(Restrictions.eq("line1", line1));
            addressCriteria.add(Restrictions.eq("line2", line2));
            addressCriteria.add(Restrictions.eq("postal_code", postalCode));
            addressCriteria.add(Restrictions.eq("mobile_no", mobile));
            addressCriteria.add(Restrictions.eq("province_id", province));
            addressCriteria.add(Restrictions.eq("distric_id", distric));
            addressCriteria.add(Restrictions.eq("city_id", city));
            addressCriteria.add(Restrictions.eq("user_id", user));

            Address address = (Address) addressCriteria.uniqueResult();

            if (address == null) {
                // Not found, create and save new address
                address = new Address();
                address.setLine1(line1);
                address.setLine2(line2);
                address.setPostal_code(postalCode);
                address.setMobile_no(mobile);
                address.setProvince_id(province);
                address.setDistric_id(distric);
                address.setCity_id(city);
                address.setUser_id(user);
                session.save(address);
            }

            // Fetch Cart items for this user
            Criteria cartCriteria = session.createCriteria(Cart.class);
            cartCriteria.add(Restrictions.eq("user_id", user));
            List<Cart> cartList = cartCriteria.list();

            // Cart empty check
            if (cartList == null || cartList.isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Your cart is empty. Please add items before proceeding to checkout.");
                response.getWriter().write(gson.toJson(responseObject));
                // DO NOT close session here
                return;
            }

            // Create order
            Orders order = new Orders();
            order.setCreated_at(new Date());
            order.setUser(user);
            order.setAddress(address);
            session.save(order);

            double totalAmount = 0;
            StringBuilder itemSummary = new StringBuilder();

            for (Cart cart : cartList) {

                Product product = cart.getProduct_id();

                int qty = cart.getQty();

                OrderItems orderItem = new OrderItems();

                orderItem.setOrders(order);
                orderItem.setOrderStatus(pendingStatus);
                orderItem.setProduct(product);
                orderItem.setQty(qty);
                orderItem.setRating(0);

                // Determine delivery type by city name
                DiliveryType deliveryType = city.getName().equalsIgnoreCase("Colombo") ? withinColombo : outColombo;

                orderItem.setDiliveryType(deliveryType);

                // Calculate line total (product price + delivery price) * qty
                double lineAmount = qty * (product.getPrice() + deliveryType.getPrice());

                totalAmount += lineAmount;

                itemSummary.append(product.getTitle()).append(" x ").append(qty).append(", ");

                // Update product stock
                product.setQty(product.getQty() - qty);
                session.update(product);

                // Save order item and remove from cart
                session.save(orderItem);
                session.delete(cart);
            }

            tr.commit();

            // Prepare item summary text for PayHere display
            String items = itemSummary.toString();
            if (items.endsWith(", ")) {
                items = items.substring(0, items.length() - 2);
            }

            // Merchant credentials
            String merchantID = "1224547";
            String merchantSecret = "MzEyODM2NDYzNjMxNjAzNDY1MjMyMDMzNDMxNTAzMTQ4NjYyOTkwMA==";

            // Order details
            String orderID = "#ORD000" + order.getId();
            String currency = "LKR";
            String formattedAmount = new DecimalFormat("0.00").format(totalAmount);

            // Generate secure hash
            String hash = PayHere.genarateMD5(merchantID + orderID + formattedAmount + currency + PayHere.genarateMD5(merchantSecret));

            // Create PayHere JSON payload
            JsonObject payHereJson = new JsonObject();
            payHereJson.addProperty("sandbox", true);
            payHereJson.addProperty("merchant_id", merchantID);
            payHereJson.addProperty("return_url", "");
            payHereJson.addProperty("cancel_url", "");
            payHereJson.addProperty("notify_url", "https://e87f6fe97675.ngrok-free.app/Drone_Stores/VerifyPayments");

            payHereJson.addProperty("order_id", orderID);
            payHereJson.addProperty("items", items);
            payHereJson.addProperty("amount", formattedAmount);
            payHereJson.addProperty("currency", currency);
            payHereJson.addProperty("hash", hash);

            payHereJson.addProperty("first_name", user.getFirst_name());
            payHereJson.addProperty("last_name", user.getLast_name());
            payHereJson.addProperty("email", user.getEmail());

            payHereJson.addProperty("phone", address.getMobile_no());
            payHereJson.addProperty("address", address.getLine1() + ", " + address.getLine2());
            payHereJson.addProperty("city", address.getCity_id().getName());
            payHereJson.addProperty("country", "Sri Lanka");

            // Send response back to client
            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Checkout completed");
            responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));

        } catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Checkout failed: " + e.getMessage());
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        Gson gson = new Gson();
//
//        JsonObject reqJson = gson.fromJson(request.getReader(), JsonObject.class);
//
//        JsonObject responseObject = new JsonObject();
//
//        HttpSession httpSession = request.getSession();
//
//        User user = (User) httpSession.getAttribute("user");
//
//        if (user == null) {
//
//            responseObject.addProperty("status", false);
//
//            responseObject.addProperty("message", "Session expired. Please log in again.");
//
//            response.getWriter().write(gson.toJson(responseObject));
//
//            return;
//
//        }
//
//        // Extract fields from JSON
//        String name = reqJson.get("Name").getAsString();
//        String email = reqJson.get("Email").getAsString();
//        String line1 = reqJson.get("AdLine1").getAsString();
//        String line2 = reqJson.get("AdLine2").getAsString();
//        String districtName = reqJson.get("Distric").getAsString();
//        String cityName = reqJson.get("city").getAsString();
//        String provinceName = reqJson.get("Province").getAsString();
//        String postalCode = reqJson.get("PostalCode").getAsString();
//        String mobile = reqJson.get("MobileNo").getAsString();
//
//        // Validate all fields
//        if (name.isEmpty() || email.isEmpty() || line1.isEmpty() || line2.isEmpty()
//                || districtName.isEmpty() || cityName.isEmpty() || provinceName.isEmpty()
//                || postalCode.isEmpty() || mobile.isEmpty()) {
//
//            responseObject.addProperty("status", false);
//
//            responseObject.addProperty("message", "All fields are required.");
//
//            response.getWriter().write(gson.toJson(responseObject));
//
//            return;
//
//        }
//
//        SessionFactory sf = HibernateUtil.getSessionFactory();
//        Session session = sf.openSession();
//        Transaction tr = session.beginTransaction();
//
//        try {
//
//            // Find Province by name
//            Criteria provinceCriteria = session.createCriteria(Province.class);
//            provinceCriteria.add(Restrictions.eq("name", provinceName));
//            Province province = (Province) provinceCriteria.uniqueResult();
//
//            if (province == null) {
//
//                throw new Exception("Invalid province name");
//
//            }
//
//            // Find Distric by name and province
//            Criteria districCriteria = session.createCriteria(Distric.class);
//            districCriteria.add(Restrictions.eq("name", districtName));
//            districCriteria.add(Restrictions.eq("province_id", province));
//            Distric distric = (Distric) districCriteria.uniqueResult();
//
//            if (distric == null) {
//
//                throw new Exception("Invalid district name for given province");
//
//            }
//
//            // Find City by name and district
//            Criteria cityCriteria = session.createCriteria(City.class);
//            cityCriteria.add(Restrictions.eq("name", cityName));
//            cityCriteria.add(Restrictions.eq("distric_id", distric));
//            City city = (City) cityCriteria.uniqueResult();
//
//            if (city == null) {
//
//                throw new Exception("Invalid city name for given district");
//
//            }
//
//            // Get pending order status
//            OrderStatus pendingStatus = (OrderStatus) session.get(OrderStatus.class, ORDER_PENDING_ID);
//            DiliveryType withinColombo = (DiliveryType) session.get(DiliveryType.class, DELIVERY_WITHIN_COLOMBO);
//            DiliveryType outColombo = (DiliveryType) session.get(DiliveryType.class, DELIVERY_OUT_OF_COLOMBO);
//
//            // Check if address already exists for this user with all given details
//            Criteria addressCriteria = session.createCriteria(Address.class);
//            addressCriteria.add(Restrictions.eq("line1", line1));
//            addressCriteria.add(Restrictions.eq("line2", line2));
//            addressCriteria.add(Restrictions.eq("postal_code", postalCode));
//            addressCriteria.add(Restrictions.eq("mobile_no", mobile));
//            addressCriteria.add(Restrictions.eq("province_id", province));
//            addressCriteria.add(Restrictions.eq("distric_id", distric));
//            addressCriteria.add(Restrictions.eq("city_id", city));
//            addressCriteria.add(Restrictions.eq("user_id", user));
//
//            Address address = (Address) addressCriteria.uniqueResult();
//
//            if (address == null) {
//
//                // Not found, create and save new address
//                address = new Address();
//                address.setLine1(line1);
//                address.setLine2(line2);
//                address.setPostal_code(postalCode);
//                address.setMobile_no(mobile);
//                address.setProvince_id(province);
//                address.setDistric_id(distric);
//                address.setCity_id(city);
//                address.setUser_id(user);
//                session.save(address);
//
//            }
//
//            // Then use this 'address' (existing or new) in order
//            Orders order = new Orders();
//            order.setCreated_at(new Date());
//            order.setUser(user);
//            order.setAddress(address);
//            session.save(order);
//
////            // Generate and save formatted order ID
////            int orderId = 100000 + new Random().nextInt(900000);
////            order.setId(orderId);
////            session.update(order);
//
//            // Fetch Cart items for this user
//            Criteria cartCriteria = session.createCriteria(Cart.class);
//            cartCriteria.add(Restrictions.eq("user_id", user));
//            List<Cart> cartList = cartCriteria.list();
//
//            double totalAmount = 0;
//            StringBuilder itemSummary = new StringBuilder();
//
//            for (Cart cart : cartList) {
//
//                Product product = cart.getProduct_id();
//
//                int qty = cart.getQty();
//
//                OrderItems orderItem = new OrderItems();
//
//                orderItem.setOrders(order);
//
//                orderItem.setOrderStatus(pendingStatus);
//
//                orderItem.setProduct(product);
//
//                orderItem.setQty(qty);
//
//                orderItem.setRating(0);
//
//                // Determine delivery type by city name
//                DiliveryType deliveryType = city.getName().equalsIgnoreCase("Colombo") ? withinColombo : outColombo;
//
//                orderItem.setDiliveryType(deliveryType);
//
//                // Calculate line total (product price + delivery price) * qty
//                double lineAmount = qty * (product.getPrice() + deliveryType.getPrice());
//
//                totalAmount += lineAmount;
//
//                itemSummary.append(product.getTitle()).append(" x ").append(qty).append(", ");
//
//                // Update product stock
//                product.setQty(product.getQty() - qty);
//
//                session.update(product);
//
//                // Save order item and remove from cart
//                session.save(orderItem);
//
//                session.delete(cart);
//
//            }
//
//            tr.commit();
//            
//            //Prepare item summary text for PayHere display
//            String items = itemSummary.toString();
//            if (items.endsWith(", ")) {
//                items = items.substring(0, items.length() - 2);
//            }
//
//            // Merchant credentials
//            String merchantID = "1224547";
//            String merchantSecret = "MzEyODM2NDYzNjMxNjAzNDY1MjMyMDMzNDMxNTAzMTQ4NjYyOTkwMA==";
//
//            // Order details
//            String orderID = "#ORD" + order;
//            String currency = "LKR";
//            String formattedAmount = new DecimalFormat("0.00").format(totalAmount);
//
//            // Generate secure hash
//            String hash = PayHere.genarateMD5(merchantID + orderID + formattedAmount + currency + PayHere.genarateMD5(merchantSecret));
//
//            // Create PayHere JSON payload
//            JsonObject payHereJson = new JsonObject();
//            payHereJson.addProperty("sandbox", true);
//            payHereJson.addProperty("merchant_id", merchantID);
//            payHereJson.addProperty("return_url", ""); 
//            payHereJson.addProperty("cancel_url", "");
//            payHereJson.addProperty("notify_url", "https://e87f6fe97675.ngrok-free.app/Drone_Stores/VerifyPayments");
//
//            payHereJson.addProperty("order_id", orderID);
//            payHereJson.addProperty("items", items);
//            payHereJson.addProperty("amount", formattedAmount);
//            payHereJson.addProperty("currency", currency);
//            payHereJson.addProperty("hash", hash);
//
//            payHereJson.addProperty("first_name", user.getFirst_name());
//            payHereJson.addProperty("last_name", user.getLast_name());
//            payHereJson.addProperty("email", user.getEmail());
//
//            payHereJson.addProperty("phone", address.getMobile_no());
//            payHereJson.addProperty("address", address.getLine1() + ", " + address.getLine2());
//            payHereJson.addProperty("city", address.getCity_id().getName());
//            payHereJson.addProperty("country", "Sri Lanka");
//
//            //Send response back to client
//            responseObject.addProperty("status", true);
//            responseObject.addProperty("message", "Checkout completed");
//            responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));
//
//        } catch (Exception e) {
//
//            if (tr != null) {
//
//                tr.rollback();
//
//            }
//
//            responseObject.addProperty("status", false);
//
//            responseObject.addProperty("message", "Checkout failed: " + e.getMessage());
//
//        } finally {
//
//            session.close();
//
//        }
//
//        response.setContentType("application/json");
//
//        response.getWriter().write(gson.toJson(responseObject));
//
//    }
}
