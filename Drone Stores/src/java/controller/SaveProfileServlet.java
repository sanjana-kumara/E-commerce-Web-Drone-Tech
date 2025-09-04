/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.Distric;
import hibernate.HibernateUtil;
import hibernate.Province;
import hibernate.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sanjana
 */
@WebServlet(name = "SaveProfileServlet", urlPatterns = {"/SaveProfileServlet"})
public class SaveProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {

            User user = (User) ses.getAttribute("user");
            JsonObject respJsonObject = new JsonObject();

            respJsonObject.addProperty("firstName", user.getFirst_name());
            respJsonObject.addProperty("lastName", user.getLast_name());
            respJsonObject.addProperty("email", user.getEmail());

            Gson gson = new Gson();
            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c = s.createCriteria(Address.class);
            c.add(Restrictions.eq("user_id", user));

            if (!c.list().isEmpty()) {

                List<Address> addressList = c.list();

                respJsonObject.add("addressList", gson.toJsonTree(addressList));

            }

            String toJson = gson.toJson(respJsonObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);

        }

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        // Extract values
        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String mobileNo = userData.get("mobileNo").getAsString();
        String lineOne = userData.get("address1").getAsString();
        String lineTwo = userData.get("address2").getAsString();
        String postalCode = userData.get("postalCode").getAsString();

        int provinceId = parseIntSafe(userData, "provinceId");
        int districtId = parseIntSafe(userData, "districtId");
        int cityId = parseIntSafe(userData, "cityId");

        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(mobileNo);
        System.out.println(lineOne);
        System.out.println(lineTwo);
        System.out.println(postalCode);
        System.out.println(provinceId);
        System.out.println(districtId);
        System.out.println(cityId);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {

            responseObject.addProperty("message", "First Name can not be empty!");

        } else if (lastName.isEmpty()) {

            responseObject.addProperty("message", "Last Name can not be empty!");

        } else if (mobileNo.isEmpty()) {

            responseObject.addProperty("message", "Mobile can not be empty!");

        } else if (!Util.isMobileValid(mobileNo)) {

            responseObject.addProperty("message", "Enter Valide Mobile Number!");

        } else if (lineOne.isEmpty()) {

            responseObject.addProperty("message", "Enter address line one");

        } else if (lineTwo.isEmpty()) {

            responseObject.addProperty("message", "Enter address line two");

        } else if (postalCode.isEmpty()) {

            responseObject.addProperty("message", "Enter your postal code");

        } else if (!Util.isCodeValid(postalCode)) {

            responseObject.addProperty("message", "Enter correct postal code");

        } else if (provinceId == 0) {

            responseObject.addProperty("message", "Select a Province");

        } else if (districtId == 0) {

            responseObject.addProperty("message", "Select a District");

        } else if (cityId == 0) {

            responseObject.addProperty("message", "Select a city");

        } else {

            HttpSession ses = request.getSession();

            if (ses.getAttribute("user") != null) {

                User U = (User) ses.getAttribute("user");

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c = s.createCriteria(User.class);

                c.add(Restrictions.eq("email", U.getEmail()));

                if (!c.list().isEmpty()) {

                    User u1 = (User) c.list().get(0);

                    Province province = (Province) s.load(Province.class, provinceId);
                    Distric distric = (Distric) s.load(Distric.class, districtId);
                    City city = (City) s.load(City.class, cityId);
                    Address address = new Address();
                    address.setLine1(lineOne);
                    address.setLine2(lineTwo);
                    address.setPostal_code(postalCode);
                    address.setMobile_no(mobileNo);
                    address.setProvince_id(province);
                    address.setDistric_id(distric);
                    address.setCity_id(city);
                    address.setUser_id(u1);

                    ses.setAttribute("user", u1);

                    s.merge(u1);
                    s.save(address);
                    s.beginTransaction().commit();
                    s.close();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "User profile deatils update successfully!!");

                }

            }

        }

        String responseText = gson.toJson(responseObject);

        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

    // Helper method to parse Int safely
    private int parseIntSafe(JsonObject obj, String key) {

        if (obj.has(key) && !obj.get(key).isJsonNull()) {

            String value = obj.get(key).getAsString();

            if (!value.trim().isEmpty()) {

                return Integer.parseInt(value);

            }

        }

        return 0;

    }

}
