/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "GetProfileServlet", urlPatterns = {"/GetProfileServlet"})
public class GetProfileServlet extends HttpServlet {

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        Gson gson = new Gson();
//        JsonObject jsonResponse = new JsonObject();
//        jsonResponse.addProperty("status", false);
//
//        HttpSession session = request.getSession(false);
//
//        if (session != null && session.getAttribute("user") != null) {
//            
//            User user = (User) session.getAttribute("user");
//
//            Session s = HibernateUtil.getSessionFactory().openSession();
//            Criteria c = s.createCriteria(Address.class);
//            c.add(Restrictions.eq("user_id", user));
//
//            Address address = (Address) c.uniqueResult();
//
//            if (address != null) {
//                
//                JsonObject userObj = new JsonObject();
//                userObj.addProperty("firstName", user.getFirst_name());
//                userObj.addProperty("lastName", user.getLast_name());
//                userObj.addProperty("email", user.getEmail());
//                userObj.addProperty("mobileNo", address.getMobile_no());
//                userObj.addProperty("address1", address.getLine1());
//                userObj.addProperty("address2", address.getLine2());
//                userObj.addProperty("postalCode", address.getPostal_code());
//
//                // Send selected IDs for dropdowns
//                userObj.addProperty("provinceId", address.getProvince_id().getId());
//                userObj.addProperty("districtId", address.getDistric_id().getId());
//                userObj.addProperty("cityId", address.getCity_id().getId());
//
//                jsonResponse.add("data", userObj);
//                jsonResponse.addProperty("status", true);
//                
//            }
//
//            s.close();
//        }
//
//        response.setContentType("application/json");
//        response.getWriter().write(gson.toJson(jsonResponse));
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c = s.createCriteria(Address.class);
            c.add(Restrictions.eq("user_id", user));
            Address address = (Address) c.uniqueResult();

            if (address != null) {
                JsonObject data = new JsonObject();
                data.addProperty("firstName", user.getFirst_name());
                data.addProperty("lastName", user.getLast_name());
                data.addProperty("email", user.getEmail());
                data.addProperty("mobileNo", address.getMobile_no());
                data.addProperty("address1", address.getLine1());
                data.addProperty("address2", address.getLine2());
                data.addProperty("postalCode", address.getPostal_code());
                data.addProperty("provinceId", address.getProvince_id().getId());
                data.addProperty("districtId", address.getDistric_id().getId());
                data.addProperty("cityId", address.getCity_id().getId());

                responseObject.add("data", data);

                System.out.println(data);

                responseObject.addProperty("status", true);
            }

            s.close();
        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

}
